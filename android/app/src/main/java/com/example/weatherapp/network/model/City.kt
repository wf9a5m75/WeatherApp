package com.example.weatherapp.network.model

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: String,
    val name: String
)
