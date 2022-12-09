package com.example.weatherapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Forecast(
    val time: String,
    val temperature: Double,
    val status: String
)
