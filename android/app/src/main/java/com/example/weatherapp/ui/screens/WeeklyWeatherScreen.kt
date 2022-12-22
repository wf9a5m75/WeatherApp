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
import androidx.lifecycle.viewmodel.compose.*
import com.example.weatherapp.AppViewModel

@Composable
fun WeeklyWeatherScreen(viewModel: AppViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray),
    ) {
        Button(onClick = {
            Log.d("Weekly", "--->clicked")
        }) {
            Text("Weekly weather!!")
        }
    }
}
