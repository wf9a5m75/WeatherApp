package com.example.weatherapp.model

import android.content.Context
import android.util.Log
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

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

class Prefecture(
    val id: String,
    val name: String,
    val cities: List<City>
)

data class City(
    val id: String,
    val name: String
)



interface WeatherApi {
    @GET("/api/v1/locations")
    suspend fun getLocations(): Response<LocationResponse>
}



object RetrofitHelper {
    val baseUrl = "https://alpha2022-mk-chat.web.app/"

    var _instance: Retrofit? = null

    fun getInstance(context: Context): Retrofit {
        if (_instance != null) {
            Log.d("weather", "--->cache hit!")
            return _instance!!
        }
        var cacheSize = 10 * 1024 * 1024L // 1 MB
        var cache: Cache = Cache(context.cacheDir, cacheSize)

        val clint = OkHttpClient.Builder()
            .cache(cache)
            .build()

        _instance = Retrofit.Builder().baseUrl(baseUrl)
            .client(clint)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        Log.d("weather", "--->create new instance!")
        return _instance!!
    }
}