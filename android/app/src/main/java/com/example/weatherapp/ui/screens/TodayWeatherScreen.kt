package com.example.weatherapp.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.example.weatherapp.R
import com.example.weatherapp.model.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview(showBackground = true)
@Composable
fun TodayWeatherScreen(viewModel: AppViewModel = viewModel()) {
    val refreshState = rememberSwipeRefreshState(isRefreshing = false)
    val scrollState = rememberScrollState()

    val onRefresh: (refreshState: SwipeRefreshState) -> Unit = { state ->
        if (viewModel.isOnline) {
            state.isRefreshing = true

            viewModel.getForecastFromServer()

            state.isRefreshing = false
        }
    }
    onRefresh(refreshState)

    SwipeRefresh(state = refreshState, onRefresh = {
        onRefresh(refreshState)
    }) {
        Image(
            painter = painterResource(id = R.drawable.bg_sunny),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = viewModel.lastUpdateTime,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 32.sp,
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.body2
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .verticalScroll(state = scrollState, enabled = true)
        ) {

            Image(
                painter = weatherIconResource(viewModel.currentWeatherIcon, getCurrentHour()),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(400.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(all = 32.dp)
            )
            Text(
                text = "18℃",
                fontSize = 64.sp,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.body2

            )
            Row(
                modifier = Modifier.fillMaxSize()
            ) {

                WeatherIcon(
                    weather = "sunny",
                    temperature = 18,
                    hour24 = 12,
                    modifier = Modifier.weight(1f)
                )
                WeatherIcon(
                    weather = "sunny",
                    temperature = 17,
                    hour24 = 15,
                    modifier = Modifier.weight(1f)
                )
                WeatherIcon(
                    weather = "sunny",
                    temperature = 15,
                    hour24 = 18,
                    modifier = Modifier.weight(1f)
                )
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
            text = "${temperature}℃",
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSecondary,
            style = MaterialTheme.typography.body2
        )

    }
}
