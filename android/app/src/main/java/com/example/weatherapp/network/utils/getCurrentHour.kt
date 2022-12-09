package com.example.weatherapp.network.utils

import androidx.compose.runtime.Composable
import java.util.Calendar

@Composable
fun getCurrentHour(): Int {
    val now = Calendar.getInstance()
    return now.get(Calendar.HOUR_OF_DAY)
}
