package com.example.weatherapp.model

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.weatherapp.R
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Calendar

/**
 *  ref: Retrofit with Kotlin Coroutine in Android
 *  https://www.geeksforgeeks.org/retrofit-with-kotlin-coroutine-in-android/
 *
 *  ref: Retrofit with cache
 *  https://futurestud.io/tutorials/retrofit-2-activate-response-caching-etag-last-modified
 */

data class LocationResponse(
    val last_update: String,
    val prefectures: List<Prefecture>
)

data class Prefecture(
    val id: String,
    val name: String,
    val cities: List<City>
)

data class City(
    val id: String,
    val name: String
)

data class ForecastResponse(
    val last_update: String,
    val overall: String,
    val forecasts: List<Forecast>
)

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


object RetrofitHelper {
    private const val baseUrl = "https://weather-app-8a034.web.app"

    private var instance: Retrofit? = null

    fun getInstance(context: Context): Retrofit {
        if (this.instance != null) {
            return this.instance!!
        }
        val cacheSize = 10 * 1024 * 1024L // 1 MB
        val cache = Cache(context.cacheDir, cacheSize)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = when(context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
            0 -> HttpLoggingInterceptor.Level.BODY
            else -> HttpLoggingInterceptor.Level.NONE
        }

        val clint = OkHttpClient.Builder()
            .cache(cache)
            .followRedirects(true)
            .followSslRedirects(true)
            .addNetworkInterceptor(interceptor)
            .build()

        this.instance = Retrofit.Builder().baseUrl(baseUrl)
            .client(clint)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return this.instance!!
    }
}

class WeatherApi(context: Context) {
    private var instance: IWeatherApi

    init {
        this.instance = RetrofitHelper.getInstance(context).create(IWeatherApi::class.java)
    }

    suspend fun getLocationsFromServer(): Response<LocationResponse> {

        return this.instance.getLocations()
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
    Log.d("debug", "hour24 = ${hour24} -> isDayTime = ${isDayTime}")

    return painterResource(id = when(weather) {
        "sunny" -> when(isDayTime) {
            true -> R.drawable.wt_clear_day
            else -> R.drawable.wt_clear_night
        }
        "cloudy" -> when(isDayTime) {
            true -> R.drawable.wt_cloud_day
            else -> R.drawable.wt_cloud_night
        }
        "rain" -> when(isDayTime) {
            true -> R.drawable.wt_rain_day
            else -> R.drawable.wt_rain_night
        }
        "snow" -> when(isDayTime) {
            true -> R.drawable.wt_snow_day
            else -> R.drawable.wt_snow_night
        }


        else -> R.drawable.wt_unknown
    })
}

@Composable
fun getCurrentHour(): Int {
    val now = Calendar.getInstance()
    return now.get(Calendar.HOUR_OF_DAY)
}