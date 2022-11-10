package com.example.weatherapp.ui

import android.util.Log
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
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@ExperimentalUnitApi
@Composable
fun AppTabs(pages: List<Unit>) {
    val tabTitles = listOf("One", "Two", "Three")
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val bgColors = listOf<Color>(Color.Red, Color.Green, Color.Blue)

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

            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        Log.d("test", "tab should be ${index}")
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
            tabIndex ->

                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(bgColors[tabIndex])) {

                    Text(
                        "${tabIndex.toString()} page",
                        modifier = Modifier
                            .size(20.dp)
                    )
                    Button(onClick = { /*TODO*/ }) {
                        Text("Click me!")
                    }
                }
        }
    }
}