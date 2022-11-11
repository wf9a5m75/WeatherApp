package com.example.weatherapp.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.ui.screens.TodayWeatherScreen
import com.example.weatherapp.ui.screens.TomorrowWeatherScreen
import com.example.weatherapp.ui.screens.WeeklyWeatherScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalPagerApi::class)
@ExperimentalUnitApi
@Composable
fun AppTabs(context: Context) {
    val tabTitles = listOf<String>(
        stringResource(R.string.tab_today),
        stringResource(R.string.tab_tomorrow),
        stringResource(R.string.tab_weekly)
    )
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = {
                tabPositions -> TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(
                        pagerState,
                        tabPositions
                    )
                )
            }
        ) {

            // Move the tab indicator when you tap on a tab
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        // Change the current tab view with animation
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(text = title)
                    }
                )
            }
        }


        HorizontalPager(
            count = tabTitles.size,
            state = pagerState
        ) {
            // Invoked when selected tab has been changed
            tabIndex ->
                when(tabIndex) {
                    0 -> showTodayScreen(context = context)

                    1 -> showTomorrowScreen(context = context)

                    2 -> showWeeklyScreen(context = context)
                }
        }
    }
}

@Composable
fun showTodayScreen(context: Context) {
    TodayWeatherScreen(
        onClick = {
            Toast.makeText(context, "This is today screen!", Toast.LENGTH_SHORT).show()
        }
    )
}

@Composable
fun showTomorrowScreen(context: Context) {
    TomorrowWeatherScreen(
        onClick = {
            Toast.makeText(context, "This is tomorrow screen!", Toast.LENGTH_SHORT).show()
        }
    )
}

@Composable
fun showWeeklyScreen(context: Context) {
    WeeklyWeatherScreen(
        onClick = {
            Toast.makeText(context, "This is weekly screen!", Toast.LENGTH_SHORT).show()
        }
    )
}