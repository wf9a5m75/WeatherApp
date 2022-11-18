package com.example.weatherapp.model

import android.net.ConnectivityManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.utils.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection


class AppViewModel() : ViewModel() {
    var isOnline by mutableStateOf(false)

    lateinit var connectivityManager: ConnectivityManager
    lateinit var weatherApi: WeatherApi
    lateinit var datastore: DataStore<Preferences>

    var city by mutableStateOf(City("", ""))

    val cities: MutableList<Prefecture> = mutableListOf()

    fun loadPrefCity() {
        viewModelScope.launch {
            internalLoadPrefCity(datastore)
        }
    }

    private suspend fun internalLoadPrefCity(datastore: DataStore<Preferences>) =
        withContext(Dispatchers.IO) {
            city = loadPrefCity(datastore)
        }

    fun getLocationsFromServer(
        // TODO: 引数を適切なものに変える
        aaa: (String) -> Unit,
    ) {
        viewModelScope.launch {
            internalGetLocationsFromServer { direction ->
                aaa(direction)
            }
        }
    }


    private suspend fun internalGetLocationsFromServer(
        // TODO: 引数を適切なものに変える
        aaa: (String) -> Unit,
    ) = withContext(Dispatchers.IO) {

        // -------------------------------------------
        //  Load setting values from Database
        // -------------------------------------------
        internalLoadPrefCity(datastore)

        val response = weatherApi.getLocationsFromServer()
        Log.d("loading", "response.code() = $response.code()")

        when (response.code()) {
            HttpURLConnection.HTTP_OK -> {
                val result = response.body()!!
                cities.clear()
                cities.addAll(result.prefectures)

                val direction = if (city.id.isEmpty()) {
                    "settings"
                } else {
                    "main"
                }

                withContext(Dispatchers.Main) {
                    aaa(direction)
                }
            }
            else -> {
                isOnline = false
            }
        }
    }

    fun getForecastFromServer(day: Int = 0) {
        viewModelScope.launch {
            internalGetForecastFromServer(day)
        }
    }

    var currentWeatherIcon by mutableStateOf("sunny")
    var lastUpdateTime by mutableStateOf("")

    private suspend fun internalGetForecastFromServer(day: Int) = withContext(Dispatchers.IO) {
        val response = weatherApi.getForecastFromServer(city, day = day)

        val forecastRes = response.body() ?: return@withContext

        currentWeatherIcon = forecastRes.forecasts[0].status
        lastUpdateTime = forecastRes.last_update

        Log.d("debug", response.body().toString())

    }

    fun checkOnline() {
        isOnline = NetworkUtil.isOnline(connectivityManager)
    }

    fun savePrefCity() {
        viewModelScope.launch {
            internalSavePrefCity()
        }
    }

    private suspend fun internalSavePrefCity() {
        savePrefCity(datastore, city)
    }
}
