package com.example.weatherapp.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.model.Settings
import com.example.weatherapp.model.WeatherApi
import com.example.weatherapp.utils.NetworkUtil
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

@Preview(showBackground = true)
@Composable
fun TodayWeatherScreen(context: Context? = null, settings: Settings? = null, api: WeatherApi? = null) {
    val refreshState = rememberSwipeRefreshState(isRefreshing = false)
    val scrollState = rememberScrollState()

    var currentWeatherIcon by remember { mutableStateOf("sunny") }
    var lastUpdateTime by remember { mutableStateOf("") }

    val onRefresh : (refreshState: SwipeRefreshState) -> Unit = {
            state ->
                if ((context != null) &&
                    (settings != null) &&
                    (api != null) &&
                    (NetworkUtil.isOnline(context))) {

                    state.isRefreshing = true

                    runBlocking {
                        val deferred = async {
                            api.getForecastFromServer(
                                city = settings.city.value,
                                day = 0
                            )
                        }
                        state.isRefreshing = false

                        val response = deferred.await()
                        if (response.code() != 200) {
                            return@runBlocking
                        }

                        val forecastRes = response.body()
                        if (forecastRes == null) {
                            return@runBlocking
                        }
                        currentWeatherIcon = forecastRes.forecasts[0].status
                        lastUpdateTime = forecastRes.last_update
                        Log.d("debug", response.body().toString())
                    }
        }

    }
    onRefresh(refreshState)
    
    SwipeRefresh(state = refreshState, onRefresh = {
        onRefresh(refreshState)
    }) {

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
                painter = weatherIconResource(currentWeatherIcon),
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
                    time = "12pm",
                    modifier = Modifier.weight(1f)
                )
                WeatherIcon(
                    weather = "sunny",
                    temperature = 18,
                    time = "3pm",
                    modifier = Modifier.weight(1f)
                )
                WeatherIcon(
                    weather = "sunny",
                    temperature = 18,
                    time = "6pm",
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
    time: String,
    modifier: Modifier = Modifier
) {
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
            painter = weatherIconResource(weather),
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

@Composable
fun weatherIconResource(weather: String): Painter {
    return when(weather) {
        "sunny" -> painterResource(id = R.drawable.cloud_sun)

        else -> painterResource(id = R.drawable.cloud_sun)
    }
}