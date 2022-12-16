package com.example.weatherapp.network

import com.example.weatherapp.network.model.ForecastResponse
import com.example.weatherapp.network.model.LocationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherApi {
    @GET("/api/v1/locations")
    suspend fun getLocations(): Response<LocationResponse>

    @GET("/api/v1/forecast")
    suspend fun getForecast(
        @Query("city_id") city_id: String,
        @Query("day") day: Int,
        @Query("_cache") cache: Boolean
    ): Response<ForecastResponse>
}
