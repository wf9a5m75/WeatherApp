package com.example.weatherapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.utils.weatherIconResource

@Composable
fun WeatherIcon(
    weather: String,
    temperature: Int,
    hour24: Int,
    modifier: Modifier = Modifier,
) {
    val time = "${hour24 % 12} ${
        when (hour24 < 12) {
            true -> "am"
            else -> "pm"
        }
    }"
    Column(
        modifier = modifier
            .size(
                width = 100.dp,
                height = 130.dp,
            )
            .wrapContentSize(Alignment.Center),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
            text = time,
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSecondary,
            style = MaterialTheme.typography.body2,
        )

        Image(
            painter = weatherIconResource(weather, hour24),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally)
                .padding(all = 3.dp),
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
            text = "$temperature℃",
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSecondary,
            style = MaterialTheme.typography.body2,
        )
    }
}
