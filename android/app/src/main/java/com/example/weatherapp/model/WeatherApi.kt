package com.example.weatherapp.model

import android.content.Context
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherapp.utils.NetworkUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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

data class Prefecture(
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
        Log.d("debug", "--->before creating cache")
        if (_instance != null) {
            return _instance!!
        }
        val cacheSize = 10 * 1024 * 1024L // 1 MB
        val cache = Cache(context.cacheDir, cacheSize)
        Log.d("debug", "--->after creating cache")

        val clint = OkHttpClient.Builder()
            .cache(cache)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()

        Log.d("debug", "--->after creating client")
        _instance = Retrofit.Builder().baseUrl(baseUrl)
            .client(clint)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        Log.d("debug", "--->after creating instance")
        return _instance!!
    }
}

suspend fun getLocationsFromServer(context: Context): Response<LocationResponse> {
    val weatherApi = RetrofitHelper.getInstance(context).create(WeatherApi::class.java)
    return weatherApi.getLocations()
}