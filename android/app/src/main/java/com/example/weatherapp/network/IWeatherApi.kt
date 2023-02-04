package com.example.weatherapp.network

import com.example.weatherapp.network.model.LocationResponse
import com.example.weatherapp.network.model.WeeklyForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherApi {

    @GET("/api/v1/locations/")
    suspend fun getLocations(): Response<LocationResponse>

    @GET("/api/v1/weekly/")
    suspend fun getWeeklyForecast(
        @Query("city_id")
        city_id: String,
    ): Response<WeeklyForecast>
}
