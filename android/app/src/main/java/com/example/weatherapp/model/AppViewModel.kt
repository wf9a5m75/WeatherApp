package com.example.weatherapp.model

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.weatherapp.utils.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.util.*

class AppViewModel : ViewModel() {

    private val TAG = "ViewModel"

    var city by mutableStateOf<City?>(null)

    val locations: MutableList<Prefecture> = mutableListOf()

    private val timers: HashMap<String, Timer> = hashMapOf()

    private var hasInitialized: Boolean = false

    lateinit var networkMonitor: NetworkMonitor

    private lateinit var weatherApi: WeatherApi

    private lateinit var appDb: AppDatabase

    private lateinit var cacheDB: CacheDB

    fun fixedRateTimer(
        name: String,
        daemon: Boolean,
        startAt: Date,
        period: Long,
        action: TimerTask.() -> Unit
    ): Timer {
        if (timers.containsKey(name)) {
            return timers.get(name)!!
        }

        val timer = kotlin.concurrent.fixedRateTimer(name, daemon, startAt, period, action)
        timers.put(name, timer)
        return timer
    }

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
    fun loadSelectedCity() = viewModelScope.launch {
        city = internalLoadSelectedCity()
        Log.d("debug", "--------->track: selectedCityId = $city")
    }

    private suspend fun internalLoadSelectedCity(): City? {
        val cityJson = readValue("selected_city")
        cityJson ?: return null

        return Json.decodeFromString<City>(cityJson)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun saveSelectedCity(
        city: City
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            saveValue("selected_city", Json.encodeToString(city))
        }
    }

    suspend fun saveValue(
        key: String,
        value: String
    ) = viewModelScope.async(Dispatchers.IO) {
        this@AppViewModel.appDb.keyValueDao().put(KeyValuePair(key, value))
    }.await()

    suspend fun readValue(
        key: String
    ) = viewModelScope.async(Dispatchers.IO) {
        return@async this@AppViewModel.appDb.keyValueDao().get(key)?.value
    }.await()

    suspend fun getLocations() = viewModelScope.launch(Dispatchers.IO) {
        internalGetLocation()
    }

    var errorMessage by mutableStateOf<String?>(null)

    private suspend fun internalGetLocation() {
        if (this@AppViewModel.networkMonitor.isOnline) {
            val response = weatherApi.getLocationsFromServer()

            when (response.code()) {
                HttpURLConnection.HTTP_OK -> {
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG, "getLocationsFromServer body null.")
                        return
                    }
                    saveLocationsToDB(body)
                }
                HttpURLConnection.HTTP_INTERNAL_ERROR -> {
                }
            }
        }
        // Read locations from the DB if offline
        if (this@AppViewModel.appDb.prefectureDao().count() > 0) {
            loadLocationsFromDB()
        } else {
            Log.w(TAG, "prefecture list size = 0")
        }
    }

    private suspend fun loadLocationsFromDB() {
        val locationList = appDb.prefectureDao().getAll()
        locationList.also {
            locations.clear()
            locations.addAll(it)
        }
    }

    private fun saveLocationsToDB(locationResponse: LocationResponse) {
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
}
