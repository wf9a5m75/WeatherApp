package com.example.weatherapp.model

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.weatherapp.utils.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.util.*

class AppViewModel : ViewModel() {

    private val TAG = "ViewModel"

    var city: MutableState<City> = mutableStateOf(
        City("", "")
    )

    val locations: MutableList<Prefecture> = mutableListOf()

    private var hasInitialized: Boolean = false

    lateinit var networkMonitor: NetworkMonitor

    private lateinit var weatherApi: WeatherApi

    private lateinit var appDb: AppDatabase

    private lateinit var cacheDB: CacheDB

    fun initialize(context: Context) {
        if (this.hasInitialized) return
        this.hasInitialized = true

        this.networkMonitor = NetworkMonitor(context)

        /*
         * Generate our Weather API
         */
        this.cacheDB = Room.databaseBuilder(
            context = context,
            klass = CacheDB::class.java,
            name = "etag-database"
        ).build()
        val retrofit = RetrofitHelper.getInstance(cacheDB).create(IWeatherApi::class.java)
        this.weatherApi = WeatherApi(retrofit)

        /*
         * database
         */
        this.appDb = Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "app-database"
        ).build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun loadSelectedCity(onFinished: () -> Unit) {
        viewModelScope.launch(context = Dispatchers.IO) {
            if (this@AppViewModel.city.value.id != "") {
                viewModelScope.launch {
                    onFinished()
                }
                return@launch
            }

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
        this@AppViewModel.appDb.keyValueDao().put(KeyValuePair(key, value))
        onFinished()
    }

    fun readValue(
        key: String,
        onFinished: (value: String?) -> Unit
    ) {
        viewModelScope.launch(context = Dispatchers.IO) {
            onFinished(this@AppViewModel.appDb.keyValueDao().get(key)?.value)
        }
    }

    fun getLocations(onFinished: () -> Unit) {
        viewModelScope.launch(context = Dispatchers.IO) {

            // Read locations from DB if offline
            if (!this@AppViewModel.networkMonitor.isOnline) {
                this@AppViewModel.locations.clear()
                this@AppViewModel.locations.addAll(appDb.prefectureDao().getAll())
                viewModelScope.launch {
                    onFinished()
                }
                return@launch
            }

            // Obtain the location list from the server if online
            val response = this@AppViewModel.weatherApi.getLocationsFromServer()
            when (response.code()) {
                HttpURLConnection.HTTP_OK -> {
                    val body = response.body() as LocationResponse?
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
        this@AppViewModel.locations.addAll(this@AppViewModel.appDb.prefectureDao().getAll())
    }

    private suspend fun saveLocationsToDB(locationResponse: LocationResponse) {
        this.appDb.prefectureDao().clear()
        this.appDb.prefectureDao().insertAll(*locationResponse.prefectures.toTypedArray())
        this.appDb.keyValueDao().put(
            KeyValuePair("location_lastupdate", locationResponse.last_update)
        )
    }

//    suspend fun getTodayWeather(day: ForecastDay) = viewModelScope.async(Dispatchers.IO) {
//
//        val response = this@AppViewModel.weatherApi.getForecastFromServer(
//            city = city.value,
//            day = day.day
//        )
//        return@async response.body()
//    }.await()

    fun setCurrentCity(city: City) {
        this.city.value = city
    }
}
