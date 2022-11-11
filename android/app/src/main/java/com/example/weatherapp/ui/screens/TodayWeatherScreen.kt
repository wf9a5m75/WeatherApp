package com.example.weatherapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.weatherapp.model.RetrofitHelper
import com.example.weatherapp.model.WeatherApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun TodayWeatherScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Yellow)
    ) {
        Button(onClick = {
            getServerResponse()
        }) {
            Text("Today weather!!")
        }
    }
}

fun getServerResponse() {
}