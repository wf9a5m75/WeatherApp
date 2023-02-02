package com.example.weatherapp.ui.components

import android.graphics.Color
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.utils.weatherIconResource

@Preview(
    showBackground = true,
    backgroundColor = Color.WHITE.toLong(),
)
@Composable
fun WeatherIcon(
    weather: String = "sunny",
    temperature: Int = 13,
    hour24: Int = 14,
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
            .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .wrapContentSize(Alignment.Center),
            text = "$temperature°",
            fontSize = 14.sp,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.body2,
        )

        Image(
            painter = weatherIconResource(weather, hour24),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(50.dp)
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally),
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .wrapContentSize(Alignment.Center),
            text = time,
            fontSize = 14.sp,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.body2,
        )
    }
}
