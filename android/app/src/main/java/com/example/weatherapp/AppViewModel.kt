package com.example.weatherapp

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.database.KeyValueDao
import com.example.weatherapp.database.KeyValuePair
import com.example.weatherapp.database.PrefectureDao
import com.example.weatherapp.network.IWeatherApi
import com.example.weatherapp.network.model.City
import com.example.weatherapp.network.model.ForecastDay
import com.example.weatherapp.network.model.ForecastResponse
import com.example.weatherapp.network.model.LocationResponse
import com.example.weatherapp.network.model.Prefecture
import com.example.weatherapp.utils.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val weatherApi: IWeatherApi,
    private val prefectureDao: PrefectureDao,
    private val keyValueDao: KeyValueDao
) : ViewModel() {

    private val TAG = "ViewModel"

    var city: MutableState<City> = mutableStateOf(
        City("", "")
    )

    val locations: MutableList<Prefecture> = mutableListOf()

    val todayForecast: MutableState<ForecastResponse?> = mutableStateOf(null)

    @OptIn(ExperimentalSerializationApi::class)
    fun loadSelectedCity(onFinished: () -> Unit) {
        viewModelScope.launch {
            if (this@AppViewModel.city.value.id != "") {
                onFinished()
                return@launch
            }

            this@AppViewModel.readValue("selected_city") {
                if (it == null) {
                    onFinished()
                    return@readValue
                }

                this@AppViewModel.city.value = Json.decodeFromString<City>(it)
                onFinished()
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun saveSelectedCity(
        city: City,
        onFinished: () -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            saveValue("selected_city", Json.encodeToString(city), onFinished)
        }
    }

    private suspend fun saveValue(
        key: String,
        value: String,
        onFinished: () -> Unit = {}
    ) {
        keyValueDao.put(KeyValuePair(key, value))
        onFinished()
    }

    private fun readValue(
        key: String,
        onFinished: (value: String?) -> Unit
    ) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val result = keyValueDao.get(key)?.value
            viewModelScope.launch {
                onFinished(result)
            }
        }
    }

    fun getLocations(onFinished: () -> Unit) {
        viewModelScope.launch(context = Dispatchers.IO) {

            // Read locations from DB if offline
            if (!this@AppViewModel.networkMonitor.isOnline) {
                this@AppViewModel.locations.clear()
                this@AppViewModel.locations.addAll(prefectureDao.getAll())
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
                    readLocationsFromDB()
                }

                else -> {
                    // TODO: install preset list from JSON file
                    Log.e(TAG, "getLocationsFromServer returns something $response")
                }
            }
            viewModelScope.launch {
                onFinished()
            }
        }
    }

    private suspend fun readLocationsFromDB() {
        this@AppViewModel.locations.clear()
        this@AppViewModel.locations.addAll(prefectureDao.getAll())
    }

    private suspend fun saveLocationsToDB(locationResponse: LocationResponse) {
        prefectureDao.clear()
        prefectureDao.insertAll(*locationResponse.prefectures.toTypedArray())
        keyValueDao.put(
            KeyValuePair("location_lastupdate", locationResponse.last_update)
        )
    }

    fun updateTodayForecast(onFinished: (isUpdated: Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            getForecast(ForecastDay.TODAY) {
                if (it == null) {
                    onFinished(false)
                    return@getForecast
                }

                todayForecast.value = it
                onFinished(true)
            }
        }
    }

    private suspend fun getForecast(
        day: ForecastDay,
        onFinished: (forecast: ForecastResponse?) -> Unit
    ) {
        if (!networkMonitor.isOnline) {
            viewModelScope.launch {
                onFinished(null)
            }
            return
        }

        val response = weatherApi.getForecast(
            city_id = city.value.id,
            day = day.day
        )
        Log.d("AppViewModel", "isOnline = ${networkMonitor.isOnline}")

        todayForecast.value = response.body()

        onFinished(response.body())
    }

//    suspend fun getTodayWeather(day: ForecastDay) = viewModelScope.async(Dispatchers.IO) {
//
//        val response = this@AppViewModel.weatherApi.getForecastFromServer(
//            city = city.value,
//            day = day.day
//        )
//        return@async response.body()
//    }.await()
}