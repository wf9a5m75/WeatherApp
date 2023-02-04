package com.example.weatherapp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherapp.network.model.DailyForecast
import com.example.weatherapp.network.model.Forecast
import kotlin.math.round

@Preview(showSystemUi = true)
@Composable
fun DailyRow(
    dailyForecast: DailyForecast = DailyForecast(
        date = "2022-12-23",
        forecasts = listOf(
            Forecast(
                "2022-12-23T00:00",
                temperature = 13.2,
                status = "sunny",
            ),
            Forecast(
                "2022-12-23T03:00",
                temperature = 13.2,
                status = "sunny",
            ),
            Forecast(
                "2022-12-23T06:00",
                temperature = 13.2,
                status = "sunny",
            ),
            Forecast(
                "2022-12-23T09:00",
                temperature = 13.2,
                status = "sunny",
            ),
            Forecast(
                "2022-12-23T12:00",
                temperature = 13.2,
                status = "sunny",
            ),
        ),
    ),
    drawBottomLine: Boolean = true,
) {
    if (dailyForecast.forecasts.isEmpty()) {
        return
    }

    val scrollState = rememberScrollState()

    val tmp = dailyForecast.date.split("-")
    val dateLabel = "${tmp[1]}/${tmp[2]}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Gray,
            )
            .wrapContentHeight()
            .padding(vertical = 5.dp),
    ) {
        Column(
            modifier = Modifier
                .size(
                    width = 50.dp,
                    height = 100.dp,
                ),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = dateLabel,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )
        }
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .scrollable(
                    scrollState,
                    orientation = Orientation.Horizontal,
                ),
        ) {
            items(
                items = dailyForecast.forecasts,
                itemContent = {
                    WeatherIcon(
                        weather = it.status,
                        temperature = round(it.temperature).toInt(),
                        hour24 = it.hours24,
                        modifier = Modifier.weight(1f),
                    )
                },
            )
        }
    }
}
