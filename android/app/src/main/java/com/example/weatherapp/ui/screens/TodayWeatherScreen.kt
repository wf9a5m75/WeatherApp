package com.example.weatherapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.*
import com.example.weatherapp.R
import com.example.weatherapp.AppViewModel
import com.example.weatherapp.network.utils.getCurrentHour
import com.example.weatherapp.network.utils.weatherIconResource
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Preview(showBackground = true)
@Composable
fun TodayWeatherScreen(viewModel: AppViewModel? = viewModel()) {
    val refreshState = rememberSwipeRefreshState(isRefreshing = false)
    val scrollState = rememberScrollState()

    var currentWeatherIcon by remember { mutableStateOf("sunny") }
    var lastUpdateTime by remember { mutableStateOf("") }

//    val onRefresh: (refreshState: SwipeRefreshState) -> Unit = { state ->
//        if ((context != null) &&
//            (viewModel != null) &&
//            (api != null) &&
//            (NetworkUtil.isOnline(context))
//        ) {
//
//            state.isRefreshing = true
//
//            runBlocking {
//                val deferred = async {
//                    api.getForecastFromServer(
//                        city = viewModel.city,
//                        day = 0
//                    )
//                }
//                state.isRefreshing = false
//
//                val response = deferred.await()
//                if (response.code() != 200) {
//                    return@runBlocking
//                }
//
//                val forecastRes = response.body()
//                if (forecastRes == null) {
//                    return@runBlocking
//                }
//                currentWeatherIcon = forecastRes.forecasts[0].status
//                lastUpdateTime = forecastRes.last_update
//                Log.d("debug", response.body().toString())
//            }
//        }
//    }
//    onRefresh(refreshState)

    SwipeRefresh(
        state = refreshState,
        onRefresh = {
//            onRefresh(refreshState)
        }
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_sunny),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = "11/12/2022 14:31",
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
                painter = weatherIconResource(currentWeatherIcon, getCurrentHour()),
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
            text = "$temperature℃",
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSecondary,
            style = MaterialTheme.typography.body2
        )
    }
}
