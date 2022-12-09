package com.example.weatherapp.model

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.utils.INetworkMonitor
import com.example.weatherapp.utils.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
    private val dispatcher: CoroutineDispatcher,
    private val networkMonitor: INetworkMonitor,
    private val weatherApi: IWeatherApi,
    private val prefectureDao: PrefectureDao,
    private val keyValueDao: KeyValueDao,
) : ViewModel() {

    private val TAG = "ViewModel"

    var city: MutableState<City> = mutableStateOf(
        City("", "")
    )

    val locations: MutableList<Prefecture> = mutableListOf()

    @OptIn(ExperimentalSerializationApi::class)
    fun loadSelectedCity(onFinished: () -> Unit) {
        viewModelScope.launch(dispatcher) {
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
        viewModelScope.launch(dispatcher) {
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
        viewModelScope.launch(dispatcher) {
            val result = keyValueDao.get(key)?.value
            viewModelScope.launch {
                onFinished(result)
            }
        }
    }

    fun getLocations(onFinished: () -> Unit) {
        viewModelScope.launch(dispatcher) {

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
            val response = this@AppViewModel.weatherApi.getLocations()
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

//    suspend fun getTodayWeather(day: ForecastDay) = viewModelScope.async(dispatcher) {
//
//        val response = this@AppViewModel.weatherApi.getForecastFromServer(
//            city = city.value,
//            day = day.day
//        )
//        return@async response.body()
//    }.await()
}
