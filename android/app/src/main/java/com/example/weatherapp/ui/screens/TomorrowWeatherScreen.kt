package com.example.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun TomorrowWeatherScreen(onClick: () -> Unit = {}) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Blue)) {
        Button(onClick = onClick) {
            Text("Tomorrow weather!!")
        }
    }
}