package com.example.weatherapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.AppViewModel
import com.example.weatherapp.ui.components.DailyRow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun WeeklyWeatherScreen(viewModel: AppViewModel = viewModel()) {
    val refreshState = rememberSwipeRefreshState(isRefreshing = false)

    val onRefresh: () -> Unit = {
        refreshState.isRefreshing = true
        viewModel.updateWeeklyForecast {
            refreshState.isRefreshing = false
        }
    }

    if (viewModel.weeklyForecast.isEmpty()) {
        onRefresh()
    }

    SwipeRefresh(
        state = refreshState,
        onRefresh = onRefresh
    ) {
        LazyColumn(modifier = Modifier
            .fillMaxSize()) {
            items(
                items = viewModel.weeklyForecast,

                itemContent = {
                    DailyRow(it!!)
                }
            )
        }
    }
}
