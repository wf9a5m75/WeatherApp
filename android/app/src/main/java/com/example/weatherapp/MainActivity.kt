package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: AppViewModel by viewModels()
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

                SettingsScreen(
                    viewModel = viewModel,
                    onClose = {
                        viewModel.saveSettings()
                        navigationController.navigateUp()
                    }
                )
            }

            composable(route = "no_internet_error") {
                OfflineScreen()
            }
        }
    }

    if (!viewModel.networkMonitor.isOnline) {
        navigationController.navigateSingleTopTo("no_internet_error")
        return
    }

    viewModel.getLocations { result -> Log.d("debug", result.toString()) }

//    viewModel.getTodayWeather { code, result ->
//        if (code != HttpURLConnection.HTTP_ACCEPTED) {
//            Log.e("debug", "----->Cannot obtain forecast from server")
//            return@getTodayWeather
//        }
//
//    }

//    runBlocking {
//
//        CoroutineScope(Dispatchers.IO).launch {
//            // -------------------------------------------
//            //  Load setting values from Database
//            // -------------------------------------------
//            viewModel.city = loadPrefCity(mContext)
//
//            // -------------------------------------------
//            //  Load setting values from Database
//            // -------------------------------------------
//            if (!NetworkUtil.isOnline(mContext)) {
//                CoroutineScope(Dispatchers.Main).launch {
//                    navigationController.navigate("no_internet_error")
//                }
//            } else {
//                val locationsDeferred = async { weatherApi.getLocationsFromServer() }
//                val response = locationsDeferred.await()
//
//                when (response.code()) {
//                    200 -> {
//                        val result = response.body()!!
//                        viewModel.locations.clear()
//                        viewModel.locations.addAll(result.prefectures)
//
//                        CoroutineScope(Dispatchers.Main).launch {
//                            if (viewModel.city.id == "") {
//                                navigationController.navigate("settings")
//                            } else {
//                                navigationController.navigate("main")
//                            }
//                        }
//                    }
//
//                    else -> {
//                        CoroutineScope(Dispatchers.Main).launch {
//                            navigationController.popupToInclusive("no_internet_error")
//                        }
//                    }
//                }
//            }
//        }
//    }
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
