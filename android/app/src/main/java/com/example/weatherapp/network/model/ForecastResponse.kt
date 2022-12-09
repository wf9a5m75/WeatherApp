package com.example.weatherapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(
    val last_update: String,
    val overall: String,
    val forecasts: List<Forecast>
)
