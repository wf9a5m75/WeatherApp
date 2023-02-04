package com.example.weatherapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.weatherapp.network.model.ForecastDay
import com.example.weatherapp.ui.components.WeatherIcon
import com.example.weatherapp.utils.weatherIconResource
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.util.Calendar

@Preview()
@Composable
fun DailyWeatherScreen(
    day: ForecastDay = ForecastDay.TODAY,
    viewModel: AppViewModel = viewModel(),
) {
    val refreshState = rememberSwipeRefreshState(isRefreshing = false)
    val scrollState = rememberScrollState()
    val scrollStateForRow = rememberScrollState()

    val onRefresh: () -> Unit = {
        refreshState.isRefreshing = true
        viewModel.updateForecasts {
            refreshState.isRefreshing = false
        }
    }

    if (viewModel.forecasts.isEmpty() ||
        viewModel.forecasts[day.day]?.forecasts?.size != 24
    ) {
        if (viewModel.city.value.id.isBlank()) {
            LoadingScreen()
        } else {
            OfflineScreen()
        }
    } else {
        SwipeRefresh(
            state = refreshState,
            onRefresh = onRefresh,
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg_sunny),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Text(
                text = viewModel.sprintDateFormat(day),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontSize = 24.sp,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.body2,
            )


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .verticalScroll(state = scrollState, enabled = true),
            ) {
                Image(
                    painter = weatherIconResource(
                        "sunny", // TODO
                        12,
                    ),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(400.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(all = 32.dp),
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scrollable(
                            scrollStateForRow,
                            orientation = Orientation.Horizontal,
                        ),
                ) {
                    items(
                        items = when (day) {
                            ForecastDay.TODAY -> {
                                val nowH = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                                viewModel.forecasts[day.day]!!.forecasts.filter {
                                    it.hours24 >= nowH
                                }
                            }
                            else -> viewModel.forecasts[day.day]!!.forecasts
                        },

                        itemContent = {
                            WeatherIcon(
                                weather = it.status,
                                temperature = it.temperature.toInt(),
                                hour24 = it.hours24,
                                modifier = Modifier.weight(1f),
                            )
                        },
                    )
                }
            }
        }
    }
}
