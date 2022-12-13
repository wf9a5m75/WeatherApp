package com.example.weatherapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.AppViewModel
import com.example.weatherapp.R
import com.example.weatherapp.network.utils.getCurrentHour
import com.example.weatherapp.network.utils.weatherIconResource
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Preview(showBackground = true)
@Composable
fun TodayWeatherScreen(
    viewModel: AppViewModel = viewModel()
) {
    val refreshState = rememberSwipeRefreshState(isRefreshing = false)
    val scrollState = rememberScrollState()

    val onRefresh: (refreshState: SwipeRefreshState) -> Unit = { state ->
        state.isRefreshing = true
        viewModel.updateTodayForecast {
            state.isRefreshing = false
        }
    }
    viewModel.updateTodayForecast { }

    Log.d("TodayWeather", "---> TodayWeather : ${viewModel.todayForecast.value}")

    val nowH = getCurrentHour()

    SwipeRefresh(
        state = refreshState,
        onRefresh = {
            onRefresh(refreshState)
        }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_sunny),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = viewModel.todayForecast.value?.last_update ?: "(not available)",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 32.sp,
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.body2
        )

        if (viewModel.todayForecast.value?.forecasts?.size != 24) return@SwipeRefresh
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .verticalScroll(state = scrollState, enabled = true)
        ) {

            val currentWeather = viewModel.todayForecast.value!!.forecasts[nowH]

            Image(
                painter = weatherIconResource(
                    currentWeather.status,
                    nowH
                ),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(400.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(all = 32.dp)
            )
            Text(
                text = "${currentWeather.temperature.toInt()}℃",
                fontSize = 64.sp,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.body2
            )
            Row(
                modifier = Modifier.fillMaxSize()
            ) {

                var i = nowH + 1
                while (i < 24) {
                    val forecast = viewModel.todayForecast.value!!.forecasts[i]

                    WeatherIcon(
                        weather = forecast.status,
                        temperature = forecast.temperature.toInt(),
                        hour24 = i,
                        modifier = Modifier.weight(1f)
                    )
                    i += 3
                }
            }
        }
    }
}

@Composable
fun WeatherIcon(
    weather: String,
    temperature: Int,
    hour24: Int,
    modifier: Modifier = Modifier
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
                height = 130.dp
            )
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
            text = time,
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSecondary,
            style = MaterialTheme.typography.body2
        )

        Image(
            painter = weatherIconResource(weather, hour24),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally)
                .padding(all = 3.dp)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
            text = "$temperature℃",
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSecondary,
            style = MaterialTheme.typography.body2
        )
    }
}
