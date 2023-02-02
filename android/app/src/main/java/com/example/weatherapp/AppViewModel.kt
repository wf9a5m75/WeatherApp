package com.example.weatherapp

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.database.WeeklyForecastDao
import com.example.weatherapp.database.KeyValueDao
import com.example.weatherapp.database.KeyValuePair
import com.example.weatherapp.database.PrefectureDao
import com.example.weatherapp.network.IWeatherApi
import com.example.weatherapp.network.model.City
import com.example.weatherapp.network.model.DailyForecast
import com.example.weatherapp.network.model.ForecastDay
import com.example.weatherapp.network.model.LocationResponse
import com.example.weatherapp.network.model.Prefecture
import com.example.weatherapp.network.model.WeeklyForecast
import com.example.weatherapp.utils.INetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val networkMonitor: INetworkMonitor,
    private val weatherApi: IWeatherApi,
    private val prefectureDao: PrefectureDao,
    private val keyValueDao: KeyValueDao,
    private val weeklyForecastDao: WeeklyForecastDao,
) : ViewModel() {
    var city: MutableState<City> = mutableStateOf(
        City("", ""),
    )

    val locations = mutableStateListOf<Prefecture>()

    val forecasts = mutableStateListOf<DailyForecast?>()

    val updateForecastCallbacks = mutableListOf<()->Unit>()

    @OptIn(ExperimentalSerializationApi::class)
    fun loadSelectedCity(onFinished: () -> Unit) {
        if (this@AppViewModel.city.value.id != "") {
            onFinished()
            return
        }
        viewModelScope.launch(dispatcher) {
            this@AppViewModel.readValue("selected_city") {
                if (it == null) {
                    viewModelScope.launch {
                        onFinished()
                    }
                    return@readValue
                }

                this@AppViewModel.city.value = Json.decodeFromString<City>(it)
                viewModelScope.launch {
                    onFinished()
                }
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun saveSelectedCity(
        onFinished: () -> Unit = {},
    ) {
        viewModelScope.launch(dispatcher) {
            saveValue("selected_city", Json.encodeToString(city.value)) {
                viewModelScope.launch {
                    onFinished()
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun sprintDateFormat(day: ForecastDay): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, day.day)
        return when(day) {
            ForecastDay.TODAY -> {
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
                formatter.format(calendar.time)
            }

            else -> {
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                formatter.format(calendar.time)
            }
        }
    }

    private suspend fun saveValue(
        key: String,
        value: String,
        onFinished: () -> Unit = {},
    ) {
        keyValueDao.put(KeyValuePair(key, value))
        onFinished()
    }

    private fun readValue(
        key: String,
        onFinished: (value: String?) -> Unit,
    ) {
        viewModelScope.launch(dispatcher) {
            val result = keyValueDao.get(key)?.value
            onFinished(result)
        }
    }

    fun syncLocations(onFinished: () -> Unit) {
        viewModelScope.launch(dispatcher) {
            if ((locations.size > 0)) {
                viewModelScope.launch {
                    onFinished()
                }
                return@launch
            }

            if (!this@AppViewModel.networkMonitor.isOnline) {
                readLocationsFromDB()
                viewModelScope.launch {
                    onFinished()
                }
                return@launch
            }

            // Obtain the location list from the server if online
            val response = weatherApi.getLocations()
            when (response.code()) {
                HttpURLConnection.HTTP_OK -> {
                    val body = response.body()
                    if (body?.prefectures == null) return@launch
                    saveLocationsToDB(body)
                    readLocationsFromDB()
                }

                HttpURLConnection.HTTP_NOT_MODIFIED -> {
                    // Read values from our database
                    readLocationsFromDB()
                }

                else -> {
                    // We don't know what was occurred.
                    // Just update the list using our DB.
                    readLocationsFromDB()
                }
            }

            viewModelScope.launch {
                onFinished()
            }
        }
    }

    private suspend fun readLocationsFromDB() {
        this@AppViewModel.locations.clear()
        val results = prefectureDao.getAll()
        this@AppViewModel.locations.addAll(results)
    }

    private suspend fun saveLocationsToDB(locationResponse: LocationResponse) {
        prefectureDao.clear()
        val prefectures = locationResponse.prefectures.toTypedArray()
        prefectureDao.insertAll(*prefectures)
        keyValueDao.put(
            KeyValuePair(
                "location_lastupdate",
                locationResponse.last_update,
            ),
        )
    }

    fun updateForecasts(
        onFinished: () -> Unit
    ) {
        // If this method has been involved during processing,
        // put the callback to the buffer.
        // Resultant is the same.
        if (updateForecastCallbacks.isNotEmpty()) {
            updateForecastCallbacks.add(onFinished)
            return
        }
        updateForecastCallbacks.add(onFinished)
        viewModelScope.launch(dispatcher) {
            getWeeklyForecast {
                forecasts.clear()

                if (it != null) {
                    forecasts.addAll(it.forecasts)
                }

                // Involve the all callbacks buffered during the process
                viewModelScope.launch {
                    updateForecastCallbacks.forEach {
                        it()
                    }
                    updateForecastCallbacks.clear()
                }
            }
        }
    }

    private suspend fun getWeeklyForecast(
        onFinished: (response: WeeklyForecast?) -> Unit,
    ) {
        val cityId = city.value.id

        // If network is unavailable, read the stored data.
        if (!networkMonitor.isOnline) {
            val data = weeklyForecastDao.find(cityId)
            onFinished(data)
            return
        }


        val response = weatherApi.getWeeklyForecast(city.value.id)
        var data = response.body()

        // If we receive new data, store them into the database.
        // If no new data is available, the server replies NOT_MODIFIED(304) status code.
        // In that case, we need to use the cached data.
        if ((response.code() == HttpURLConnection.HTTP_OK) && (data != null)) {
            data.cityId = cityId
            weeklyForecastDao.put(data)
        } else {
            data = weeklyForecastDao.find(cityId)
        }
        onFinished(data)
    }
}
