package com.example.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.model.*
import com.example.weatherapp.ui.AppTabs
import com.example.weatherapp.ui.components.AppGlobalNav
import com.example.weatherapp.ui.components.OptionMenuItem
import com.example.weatherapp.ui.screens.*
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.utils.NetworkUtil

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        viewModel.datastore = this.dataStore
        viewModel.weatherApi = WeatherApi(cacheDir)

        setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    //modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    WeatherApp(viewModel = viewModel)
                }
            }
        }
    }

}

@Composable
// @Preview(showBackground = true)
fun WeatherApp(viewModel: AppViewModel) {

    val navigationController = rememberNavController()

    WeatherAppTheme {
        NavHost(navController = navigationController, startDestination = "main") {
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

                SettingsScreen(
                    viewModel = viewModel,
                    onClose = {
                        navigationController.popBackStack()
                        viewModel.savePrefCity()
                    }
                )
            }

            composable(route = "no_internet_error") {
                OfflineScreen()
            }
        }
    }

    viewModel.checkOnline()

    if (!viewModel.isOnline) {
        navigationController.navigate("no_internet_error")
        return
    }

    viewModel.getLocationsFromServer { direction ->
        navigationController.navigate(direction)
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

                    else -> { /* stub */
                    }
                }
            }
        )

        AppTabs(
            onTabChanged = { tabIndex ->
                when (tabIndex) {
                    0 -> ShowTodayScreen(
                        viewModel = viewModel,
                    )

                    1 -> ShowTomorrowScreen()

                    2 -> ShowWeeklyScreen()
                }
            }
        )

    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}

@Composable
fun SettingsScreen(
    viewModel: AppViewModel,
    onClose: () -> Unit
) {
    BackHandler(true) {
        onClose()
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SelectCityScreen(viewModel, onClose)
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


@Preview(showBackground = true)
@Composable
fun ShowTodayScreen(
    viewModel: AppViewModel = viewModel(),
) {

    TodayWeatherScreen(viewModel)
}

@Preview(showBackground = true)
@Composable
fun ShowTomorrowScreen() {
    TomorrowWeatherScreen(
        onClick = {
        }
    )
}

@Composable
fun ShowWeeklyScreen() {
    WeeklyWeatherScreen(
        onClick = {
        }
    )
}
