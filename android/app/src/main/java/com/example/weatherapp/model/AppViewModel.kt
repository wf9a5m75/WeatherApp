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
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class AppViewModel : ViewModel() {
    private val TAG = "ViewModel"

    var city: MutableState<City> = mutableStateOf(City("", ""))

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

    fun saveValue(
        key: String,
        value: String,
        onFinished: () -> Unit = {}
    ) {
        viewModelScope.launch(context = Dispatchers.IO) {
            this@AppViewModel.appDb.keyValueDao().put(KeyValuePair(key, value))
            onFinished()
        }
    }
    fun readValue(
        key: String,
        onFinished: (value: String?) -> Unit
    ) {
        viewModelScope.launch(context = Dispatchers.IO) {
            onFinished(this@AppViewModel.appDb.keyValueDao().get(key)?.value)
        }
    }

    fun getLocations(
        onFinished: (result: LocationResponse?) -> Unit
    ) {
        viewModelScope.launch {
            if (!this@AppViewModel.networkMonitor.isOnline) {

                // Read locations from the DB if offline
                if (this@AppViewModel.appDb.locaitonDao().count() > 0) {
                    onFinished(readLocationsFromDB())
                } else {
                    onFinished(null)
                }
                return@launch
            }

            // Obtain the latest location list from the server
            internalGetLocations { locations -> onFinished(locations) }
        }
    }

    private suspend fun internalGetLocations(
        onFinished: (result: LocationResponse?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val defer = async { weatherApi.getLocationsFromServer() }
        val response = defer.await()

        when (response.code()) {

            // 200: OK
            HttpURLConnection.HTTP_OK -> {
                saveLocationsToDB(response.body()!!)
                onFinished(response.body()!!)
            }

            // 304: Not modified
            HttpURLConnection.HTTP_NOT_MODIFIED -> {
                onFinished(readLocationsFromDB())
            }

            else -> onFinished(null)
        }
    }

    private fun readLocationsFromDB(): LocationResponse {
        val locations = this.appDb.locaitonDao().getAll()
        val prefToCityMap = LinkedHashMap<String, MutableList<City>>()
        val prefectureNames = LinkedHashMap<String, String>()

        locations.forEach { item ->
            when (item.kind) {
                "prefecture" -> {
                    prefectureNames[item.id] = item.value
                    prefToCityMap[item.id] = mutableListOf()
                }

                "city" -> {
                    val tmp = item.id.split(":")
                    val prefectureId = tmp[0]
                    val cityId = tmp[1]
                    prefToCityMap[prefectureId]?.add(City(cityId, item.value))
                }
            }
        }

        val prefectures = ArrayList<Prefecture>()
        for (prefectureId in prefToCityMap.keys) {
            prefectures.add(
                Prefecture(
                    id = prefectureId,
                    name = prefectureNames[prefectureId] ?: prefectureId,
                    cities = prefToCityMap[prefectureId] ?: listOf()
                )
            )
        }

        return LocationResponse(
            last_update = this.appDb.keyValueDao().get("location_lastupdate")?.value ?: "(unknown)",
            prefectures = prefectures
        )
    }

    private fun saveLocationsToDB(locationResponse: LocationResponse) {
        this.appDb.locaitonDao().clear()
        val locations = arrayListOf<LocationValue>()
        var prefCnt = 100
        locationResponse.prefectures.forEach { prefecture ->
            val prefValue = LocationValue(
                id = prefecture.id,
                value = prefecture.name,
                sortOrder = prefCnt,
                kind = "prefecture"
            )
            locations.add(prefValue)

            var cityCnt = prefCnt++
            prefecture.cities.forEach { city ->
                val cityValue = LocationValue(
                    id = "${prefecture.id}:${city.id}",
                    value = city.name,
                    kind = "city",
                    sortOrder = ++cityCnt
                )
                locations.add(cityValue)
            }
        }

        this.appDb.locaitonDao().insertAll(*locations.toTypedArray())
        this.appDb.keyValueDao().put(
            KeyValuePair("location_lastupdate", locationResponse.last_update)
        )
    }

    fun getTodayWeather(onFinished: (code: Int, result: ForecastResponse?) -> Unit) {
        viewModelScope.launch {
            internalGetWeather(ForecastDay.TODAY) { response ->
                onFinished(
                    response.code(),
                    response.body()
                )
            }
        }
    }
    private suspend fun internalGetWeather(
        day: ForecastDay,
        onFinished: (response: Response<ForecastResponse>) -> Unit
    ) = withContext(Dispatchers.IO) {
        onFinished(
            this@AppViewModel.weatherApi.getForecastFromServer(
                city = city.value,
                day = day.day
            )
        )
    }

    fun changeCity(selectedCityId: String?) {
        if (selectedCityId == null) {
            return
        }
        val location = this.appDb.locaitonDao().findByKey(selectedCityId)
        if ((location == null) || (location.kind != "city")) {
            return
        }
        this.city.value = City(location.id, location.value)
    }
}
