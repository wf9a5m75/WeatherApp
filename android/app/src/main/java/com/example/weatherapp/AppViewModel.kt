package com.example.weatherapp

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.database.KeyValueDao
import com.example.weatherapp.database.KeyValuePair
import com.example.weatherapp.database.PrefectureDao
import com.example.weatherapp.network.IWeatherApi
import com.example.weatherapp.network.model.City
import com.example.weatherapp.network.model.DailyForecast
import com.example.weatherapp.network.model.ForecastDay
import com.example.weatherapp.network.model.ForecastResponse
import com.example.weatherapp.network.model.LocationResponse
import com.example.weatherapp.network.model.Prefecture
import com.example.weatherapp.network.model.WeeklyForecastResponse
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
import javax.inject.Singleton

@HiltViewModel
class AppViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val networkMonitor: INetworkMonitor,
    private val weatherApi: IWeatherApi,
    private val prefectureDao: PrefectureDao,
    private val keyValueDao: KeyValueDao,
) : ViewModel() {
    var city: MutableState<City> = mutableStateOf(
        City("", ""),
    )

    val locations = mutableStateListOf<Prefecture>()

    val forecasts = mutableStateListOf<ForecastResponse?>(null, null)

    val weeklyForecast = mutableStateListOf<DailyForecast?>()


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

    fun updateForecast(
        day: ForecastDay,
        onFinished: (isUpdated: Boolean) -> Unit,
    ) {
        viewModelScope.launch(dispatcher) {
            if (day.day > ForecastDay.TOMORROW.day) {
                viewModelScope.launch {
                    onFinished(false)
                }
                return@launch
            }

            getForecast(day) {
                if (it == null) {
                    viewModelScope.launch {
                        onFinished(false)
                    }
                    return@getForecast
                }

                forecasts[day.day] = it
                viewModelScope.launch {
                    onFinished(true)
                }
            }
        }
    }

    private suspend fun getForecast(
        day: ForecastDay,
        onFinished: (forecast: ForecastResponse?) -> Unit,
    ) {
        if (!networkMonitor.isOnline) {
            onFinished(null)
            return
        }

        val response = weatherApi.getForecast(
            city_id = city.value.id,
            day = day.day,
        )
        onFinished(response.body())
    }


    fun updateWeeklyForecast(
        onFinished: () -> Unit
    ) {
        viewModelScope.launch {
            getWeeklyForecast {
                if (it == null) {
                    onFinished()
                    return@getWeeklyForecast
                }

                weeklyForecast.clear()
                weeklyForecast.addAll(it.dailyForecasts)
                onFinished()
            }
        }
    }

    private suspend fun getWeeklyForecast(
        onFinished: (response: WeeklyForecastResponse?) -> Unit,
    ) {
        if (!networkMonitor.isOnline) {
            onFinished(null)
            return
        }

        val response = weatherApi.getWeeklyForecast(city.value.id)
        onFinished(response.body())
    }
}
