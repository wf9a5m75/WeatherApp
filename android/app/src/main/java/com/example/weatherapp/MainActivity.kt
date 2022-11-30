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
                        navigationController.navigate("settings")
                    }
                )
            }
            composable(route = "settings") {
                SelectCityScreen(
                    locations = viewModel.locations,
                    currentCity = viewModel.city.value
                ) { selectedCity ->
                    Log.d("MainActivity", "----------->onClose: $selectedCity")
                    viewModel.city.value = selectedCity
                    viewModel.saveSelectedCity(selectedCity)
                    navigationController.popupToInclusive("main")
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
            navigationController.popupToInclusive("main")
            return@loadSelectedCity
        }

        // If no preference, move to the selectCity screen
        viewModel.getLocations {
            if (viewModel.locations.size == 0) {
                // TODO:
                Log.e("WeatherApp", "サーバーからデータの取得に失敗しました")
                return@getLocations
            }

            navigationController.popupToInclusive("settings")
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

@Composable
fun showAlert(title: String, message: String, onClosed: () -> Unit = {}) {
    val openDialog = remember { mutableStateOf(false) }
    if (!openDialog.value) {
        return
    }

    AlertDialog(
        onDismissRequest = {
            openDialog.value = false
            onClosed()
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        confirmButton = { },
        dismissButton = {
            Button(onClick = {
                openDialog.value = false
                onClosed()
            }) {
                Text(text = "閉じる")
            }
        }
    )
}
