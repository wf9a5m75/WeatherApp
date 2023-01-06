package com.example.weatherapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class WeeklyForecastResponse(
    val last_update: String,
    val dailyForecasts: List<DailyForecast>,
)
