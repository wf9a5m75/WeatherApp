package com.example.weatherapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyForecast(
    val date: String,
    val forecasts: List<Forecast>,
)
