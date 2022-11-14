package com.example.weatherapp.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.weatherapp.model.RetrofitHelper
import com.example.weatherapp.model.Settings
import com.example.weatherapp.model.WeatherApi
import com.example.weatherapp.utils.NetworkUtil
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Preview(showBackground = true)
@Composable
fun TodayWeatherScreen(context: Context? = null, settings: Settings? = null, api: WeatherApi? = null) {
    val refreshState = rememberSwipeRefreshState(isRefreshing = false)
    val scrollState = rememberScrollState()
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
                        when(response.code()) {
                            200 -> {
                                Log.d("debug", response.body().toString())
                            }

                            else -> {
                                // TODO:
                            }
                        }
                    }
        }

    }

    SwipeRefresh(state = refreshState, onRefresh = {
        onRefresh(refreshState)
    }) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState, enabled = true)
                .background(color = Color.Yellow)
        ) {
            Text("Today weather!!")
        }
    }
}
