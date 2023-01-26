package com.example.weatherapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.example.weatherapp.AppViewModel
import com.example.weatherapp.network.model.ForecastDay
import com.example.weatherapp.ui.components.AppGlobalNav
import com.example.weatherapp.ui.components.AppTabs
import com.example.weatherapp.ui.components.OptionMenuItem

@OptIn(ExperimentalUnitApi::class)
@Composable
fun MainScreen(
    viewModel: AppViewModel,
    onLocationMenuClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        AppGlobalNav(
            viewModel = viewModel,
            menuItems = listOf(
                OptionMenuItem("change_city", "場所の変更"),
            ),
            onMenuItemClicked = { menuId ->
                when (menuId) {
                    "change_city" -> onLocationMenuClicked()

                    else -> { /* stub */ }
                }
            },
        )

        AppTabs(
            onTabChanged = { tabIndex ->
                when (tabIndex) {
                    0 -> DailyWeatherScreen(ForecastDay.TODAY, viewModel)

                    1 -> DailyWeatherScreen(ForecastDay.TOMORROW, viewModel)

                    2 -> WeeklyWeatherScreen(viewModel)
                }
            },
        )
    }
}
