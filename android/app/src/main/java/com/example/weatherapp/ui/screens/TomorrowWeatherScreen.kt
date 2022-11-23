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
import com.example.weatherapp.model.AppViewModel

@Composable
fun TomorrowWeatherScreen(viewModel: AppViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Blue)
    ) {
        Button(onClick = {
            Log.d("Tomorrow", "--->clicked")
        }) {
            Text("Tomorrow weather!!")
        }
    }
}
