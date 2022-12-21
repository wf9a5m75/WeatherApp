package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.network.model.ForecastDay
import com.example.weatherapp.ui.components.AppGlobalNav
import com.example.weatherapp.ui.components.AppTabs
import com.example.weatherapp.ui.components.OptionMenuItem
import com.example.weatherapp.ui.screens.*
import com.example.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: AppViewModel by viewModels()

        setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    // modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    WeatherApp(viewModel)
                }
            }
        }
    }
}

// @Preview(showBackground = true)
@Composable
fun WeatherApp(viewModel: AppViewModel) {

    val navigationController = rememberNavController()

    WeatherAppTheme {
        NavHost(navController = navigationController, startDestination = "main") {
//            composable(route = "loading") {
//                LoadingScreen()
//            }
            composable(route = "main") {
                MainScreen(
                    viewModel = viewModel,
                    onChangeCity = {
                        navigationController.navigate("settings")
                    }
                )
            }
            composable(route = "settings") {
                SelectCityScreen(
                    viewModel = viewModel
                ) {
                    viewModel.saveSelectedCity {
                        navigationController.popupToInclusive("main")
                    }
                }
            }

            composable(route = "no_internet_error") {
                OfflineScreen()
            }
        }
    }

    // Load the last selected city
    viewModel.loadSelectedCity {
        if (viewModel.city.value.id != "") {
            return@loadSelectedCity
        }
        navigationController.popupToInclusive("settings")
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun MainScreen(
    viewModel: AppViewModel,
    onChangeCity: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppGlobalNav(
            viewModel = viewModel,
            menuItems = listOf(
                OptionMenuItem("change_city", "場所の変更")
            ),
            onMenuItemClicked = { menuId ->
                when (menuId) {
                    "change_city" -> onChangeCity()

                    else -> { /* stub */ }
                }
            }
        )

        AppTabs(
            onTabChanged = { tabIndex ->
                when (tabIndex) {
                    0 -> DailyWeatherScreen(ForecastDay.TODAY, viewModel)

                    1 -> DailyWeatherScreen(ForecastDay.TOMORROW, viewModel)

                    2 -> WeeklyWeatherScreen(viewModel)
                }
            }
        )
    }
}
fun NavHostController.popupToInclusive(route: String) = this.navigate(route) {

    // Pop up to the start destination of the graph
    // to avoid building up a large stack of destinations
    // on the back stack as users select items
    popUpTo(
        this@popupToInclusive.graph.findStartDestination().id
    ) {
        saveState = false
        inclusive = true
    }

    // Avoid multiple copies of the same destination
    // when reselecting the same item
    launchSingleTop = true

    // Restore state when reselecting a previously selected item
    restoreState = true
}
