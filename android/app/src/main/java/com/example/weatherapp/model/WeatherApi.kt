package com.example.weatherapp.model

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

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
    val temperature: Float,
    val status: String
)


interface WeatherApi {
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

        val clint = OkHttpClient.Builder()
            .cache(cache)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()

        this.instance = Retrofit.Builder().baseUrl(baseUrl)
            .client(clint)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return this.instance!!
    }
}

suspend fun getLocationsFromServer(context: Context): Response<LocationResponse> {
    val weatherApi = RetrofitHelper.getInstance(context).create(WeatherApi::class.java)
    return weatherApi.getLocations()
}