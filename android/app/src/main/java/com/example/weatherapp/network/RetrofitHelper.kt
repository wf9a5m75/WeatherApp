package com.example.weatherapp.network

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.network.cache.CacheDB
import com.example.weatherapp.network.cache.CacheDao
import com.example.weatherapp.network.cache.ETagInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object RetrofitHelper {
    private const val baseUrl = "https://weather-app-8a034.web.app"

    private var instance: Retrofit? = null

    fun getInstance(
        cacheDao: CacheDao
    ): Retrofit {
        if (this.instance != null) {
            return this.instance!!
        }

        val loggerInterceptor = HttpLoggingInterceptor()
        loggerInterceptor.level = when (BuildConfig.DEBUG) {
            true -> HttpLoggingInterceptor.Level.HEADERS
            else -> HttpLoggingInterceptor.Level.NONE
        }

        val etagInspector = ETagInterceptor(cacheDao)

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
