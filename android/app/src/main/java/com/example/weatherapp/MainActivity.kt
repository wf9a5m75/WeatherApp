package com.example.weatherapp

import android.os.Bundle
import android.util.Log
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
import com.example.weatherapp.model.AppViewModel
import com.example.weatherapp.ui.components.AppGlobalNav
import com.example.weatherapp.ui.components.AppTabs
import com.example.weatherapp.ui.components.OptionMenuItem
import com.example.weatherapp.ui.screens.*
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import java.lang.System.exit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: AppViewModel by viewModels()
        Log.d("debug", "--------->track: before initialize")
        viewModel.initialize(this)

        setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    // modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    WeatherApp(viewModel = viewModel)
                }
            }
        }
    }
}

// @Preview(showBackground = true)
@OptIn(ExperimentalSerializationApi::class)
@Composable
fun WeatherApp(viewModel: AppViewModel) {

    val navigationController = rememberNavController()
    WeatherAppTheme {
        NavHost(navController = navigationController, startDestination = "loading") {
            composable(route = "loading") {
                LoadingScreen()
            }
            composable(route = "main") {
                MainScreen(
                    viewModel = viewModel,
                    onChangeCity = {
                        navigationController.navigateSingleTopTo("settings")
                    }
                )
            }
            composable(route = "settings") {
                SelectCityScreen(
                    locations = viewModel.locations,
                    currentCity = viewModel.city.value,
                    onClose = { selectedCity ->
                        viewModel.city.value = selectedCity
                        viewModel.saveSelectedCity(selectedCity)
                        navigationController.navigateUp()
                    }
                )
            }

            composable(route = "no_internet_error") {
                OfflineScreen()
            }
        }
    }

    runBlocking {
        /**
         * Load the last selected city
         */
        val selectedCity = viewModel.loadSelectedCity()
        Log.d("debug", "--------->track: selectedCityId = $selectedCity")
        if (selectedCity != null) {
            viewModel.setCurrentCity(selectedCity)
            CoroutineScope(Dispatchers.Main).launch {
                navigationController.navigate("main")
            }
            return@runBlocking
        }

        /**
         * Show the settings screen for the first time or failed to load the settings (just in case)
         */
        val prefectures = viewModel.getLocations()
        if (prefectures == null) {
            if (!viewModel.networkMonitor.isOnline) {
                CoroutineScope(Dispatchers.Main).launch {
                    navigationController.navigateSingleTopTo("no_internet_error")
                }
            } else {
                // TODO: Error process for unknown error
                exit(1)
            }
            return@runBlocking
        }

        viewModel.locations.clear()
        viewModel.locations.addAll(prefectures)

        CoroutineScope(Dispatchers.Main).launch {
            navigationController.navigateSingleTopTo("settings")
        }
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
                    0 -> TodayWeatherScreen(viewModel)

                    1 -> TomorrowWeatherScreen(viewModel)

                    2 -> WeeklyWeatherScreen(viewModel)
                }
            }
        )
    }
}

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(route) {

    // Pop up to the start destination of the graph
    // to avoid building up a large stack of destinations
    // on the back stack as users select items
    popUpTo(
        this@navigateSingleTopTo.graph.findStartDestination().id
    ) {
        saveState = true
    }

    // Avoid multiple copies of the same destination
    // when reselecting the same item
    launchSingleTop = true

    // Restore state when reselecting a previously selected item
    restoreState = true
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
