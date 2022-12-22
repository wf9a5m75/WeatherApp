package com.example.weatherapp.network.model

import kotlinx.serialization.Serializable

/**
 *  ref: Retrofit with Kotlin Coroutine in Android
 *  https://www.geeksforgeeks.org/retrofit-with-kotlin-coroutine-in-android/
 *
 *  ref: Retrofit with cache
 *  https://futurestud.io/tutorials/retrofit-2-activate-response-caching-etag-last-modified
 */

@Serializable
data class LocationResponse(
    val last_update: String,
    val prefectures: List<Prefecture>,
)
