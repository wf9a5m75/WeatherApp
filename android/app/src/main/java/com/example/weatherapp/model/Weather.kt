package com.example.weatherapp.model

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.R
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.HttpURLConnection
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 *  ref: Retrofit with Kotlin Coroutine in Android
 *  https://www.geeksforgeeks.org/retrofit-with-kotlin-coroutine-in-android/
 *
 *  ref: Retrofit with cache
 *  https://futurestud.io/tutorials/retrofit-2-activate-response-caching-etag-last-modified
 */

@Serializable
data class LocationResponse(
    val last_update: String,
    val prefectures: List<Prefecture>
)

@Serializable
@Entity
data class Prefecture(
    @PrimaryKey val id: String,
    @ColumnInfo val name: String,
    @ColumnInfo var cities: List<City> = listOf()
)

@Serializable
class City(
    val id: String,
    val name: String
)

@Serializable
data class ForecastResponse(
    val last_update: String,
    val overall: String,
    val forecasts: List<Forecast>
)

@Serializable
data class Forecast(
    val time: String,
    val temperature: Double,
    val status: String
)

interface IWeatherApi {
    @GET("/api/v1/locations")
    suspend fun getLocations(): Response<LocationResponse>

    @GET("/api/v1/forecast")
    suspend fun getForecast(
        @Query("city_id") city_id: String,
        @Query("day") day: Int
    ): Response<ForecastResponse>
}

class DateToLongConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

@Entity
data class CacheValue(
    @PrimaryKey
    var url: String,
    @ColumnInfo
    var eTag: String,
    @ColumnInfo
    var lastModified: String
)

@Dao
interface CacheDao {

    @androidx.room.Query("SELECT * FROM CacheValue where url = :url")
    fun get(url: String): CacheValue?

    // vararg is similar to the arguments object of JS
    @Upsert
    fun put(value: CacheValue)

    @androidx.room.Query("DELETE FROM CacheValue where url = :url")
    fun remove(url: String)
}

@Database(entities = [CacheValue::class], version = 1)
@TypeConverters(DateToLongConverter::class)
abstract class CacheDB : RoomDatabase() {
    abstract fun dao(): CacheDao
}

class ETagInterceptor(db: CacheDB) : Interceptor {

    var db = db

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var request = chain.request()
        val url = request.url().toString()

        // Add "If-None-Match" header if cached ETag is available
        val cache = this.db.dao().get(url)
            ?: CacheValue(
                url = url,
                eTag = "",
                lastModified = ""
            )
        if (cache.eTag != "") {
            request = request.newBuilder()
                .addHeader("If-None-Match", cache.eTag)
                .addHeader("If-Modified-Since", cache.lastModified)
                .build()
        }

        // Process the HTTP request
        val response = chain.proceed(request)
        if ((response.code() != HttpURLConnection.HTTP_OK) ||
            response.cacheControl().noCache()
        ) {
            return response
        }

        // Save ETag and Last-Modified values to the Cache DB
        cache.eTag = response.header("ETag") ?: ""
        cache.lastModified = response.header("Last-Modified") ?: getCurrentTime()
        this.db.dao().put(cache)
        return response
    }

    private fun getCurrentTime(): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))

        val weekday = arrayOf(
            "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
        )[cal.get(Calendar.DAY_OF_WEEK) - 1]

        val month = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
            "Aug", "Sep", "Oct", "Nov", "Dec"
        ) [cal.get(Calendar.MONTH)]

        val day = cal.get(Calendar.DAY_OF_MONTH)

        val year = cal.get(Calendar.YEAR)

        val hour = "%02d".format(cal.get(Calendar.HOUR))
        val minute = "%02d".format(cal.get(Calendar.MINUTE))
        val second = "%02d".format(cal.get(Calendar.SECOND))

        // i.e. "Wed, 21 Oct 2015 07:28:00 GMT"
        return "$weekday, $day $month $year $hour:$minute:$second GMT"
    }
}
object RetrofitHelper {
    private const val baseUrl = "https://weather-app-8a034.web.app"

    private var instance: Retrofit? = null

    fun getInstance(
        eTagDb: CacheDB
    ): Retrofit {
        if (this.instance != null) {
            return this.instance!!
        }

        val loggerInterceptor = HttpLoggingInterceptor()
        loggerInterceptor.level = when (BuildConfig.DEBUG) {
            true -> HttpLoggingInterceptor.Level.HEADERS
            else -> HttpLoggingInterceptor.Level.NONE
        }

        val etagInspector = ETagInterceptor(eTagDb)

        val clint = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
//            .addInterceptor(loggerInterceptor)
            .addInterceptor(etagInspector)
            .build()

        val contentType = MediaType.get("application/json")
        this.instance = Retrofit.Builder().baseUrl(baseUrl)
            .client(clint)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()

        return this.instance!!
    }
}
enum class ForecastDay(val day: Int) {
    TODAY(0),
    TOMORROW(1),
    DAY3(2),
    DAY4(3),
    DAY5(4),
    DAY6(5),
    DAY7(6),
    DAY8(7)
}

class WeatherApi(
    instance: IWeatherApi
) {
    private val instance = instance

    suspend fun getLocationsFromServer(): Response<LocationResponse> {
        return instance.getLocations()
    }
    suspend fun getForecastFromServer(city: City, day: Int): Response<ForecastResponse> {
        return this.instance.getForecast(
            city_id = city.id,
            day = day
        )
    }
}

@Composable
fun weatherIconResource(weather: String, hour24: Int): Painter {
    val isDayTime = (hour24 >= 7) && (hour24 <= 17)
    Log.d("debug", "hour24 = $hour24 -> isDayTime = $isDayTime")

    return painterResource(
        id = when (weather) {
            "sunny" -> when (isDayTime) {
                true -> R.drawable.wt_clear_day
                else -> R.drawable.wt_clear_night
            }
            "cloudy" -> when (isDayTime) {
                true -> R.drawable.wt_cloud_day
                else -> R.drawable.wt_cloud_night
            }
            "rain" -> when (isDayTime) {
                true -> R.drawable.wt_rain_day
                else -> R.drawable.wt_rain_night
            }
            "snow" -> when (isDayTime) {
                true -> R.drawable.wt_snow_day
                else -> R.drawable.wt_snow_night
            }

            else -> R.drawable.wt_unknown
        }
    )
}

@Composable
fun getCurrentHour(): Int {
    val now = Calendar.getInstance()
    return now.get(Calendar.HOUR_OF_DAY)
}
