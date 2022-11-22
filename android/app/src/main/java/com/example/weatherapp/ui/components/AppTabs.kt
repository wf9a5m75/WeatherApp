package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.example.weatherapp.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalPagerApi::class)
@ExperimentalUnitApi
@Composable
fun AppTabs(onTabChanged: @Composable (tabIndex: Int) -> Unit) {
    val tabTitles = listOf(
        stringResource(R.string.tab_today),
        stringResource(R.string.tab_tomorrow),
        stringResource(R.string.tab_weekly)
    )
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
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
        ) { tabIndex ->
            // Invoked when selected tab has been changed
            onTabChanged(tabIndex)
        }
    }
}
