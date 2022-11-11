package com.example.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Composable
fun TodayWeatherScreen(onClick: () -> Unit = {}) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Yellow)
    ) {
        Button(onClick = onClick) {
            Text("Today weather!!")
        }
    }
}