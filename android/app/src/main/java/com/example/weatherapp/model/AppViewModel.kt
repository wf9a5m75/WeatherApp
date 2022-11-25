package com.example.weatherapp.model

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import java.util.*

class AppViewModel : ViewModel() {

    private val TAG = "ViewModel"

    var city: MutableState<City> = mutableStateOf(
        City("", "")
    )

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
    suspend fun loadSelectedCity() = viewModelScope.async(Dispatchers.IO) {
        val cityJson = readValue("selected_city")
        return@async cityJson?.let { Json.decodeFromString<City>(it) }
    }.await()

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

    suspend fun getLocations() = viewModelScope.async(Dispatchers.IO) {

        if (!this@AppViewModel.networkMonitor.isOnline) {

            // Read locations from the DB if offline
            return@async if (this@AppViewModel.appDb.prefectureDao().count() > 0) {
                readLocationsFromDB()
            } else {
                // TODO: Do something for the case, no list and internet
                null
            }
        }

        val response = weatherApi.getLocationsFromServer()
        if (response.code() == 200) {
            saveLocationsToDB(response.body()!!)
        }
        return@async readLocationsFromDB()
    }.await()

    private suspend fun readLocationsFromDB() = viewModelScope.async(Dispatchers.IO) {
        val prefectures = this@AppViewModel.appDb.prefectureDao().getAll()
        return@async prefectures
    }.await()

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

    fun setCurrentCity(city: City) {
        this.city.value = city
    }
}
