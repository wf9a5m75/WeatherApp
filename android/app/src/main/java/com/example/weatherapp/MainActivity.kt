package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.weatherapp.utils.NetworkUtil
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    //modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    WeatherApp()
                }
            }
        }
    }

}

@Composable
@Preview(showBackground = true)
fun WeatherApp(modifier: Modifier = Modifier) {
    val navigationController = rememberNavController()
    // Hold the context handle
    val mContext = LocalContext.current

    // holder for keeping setting values
    val settings = Settings(
        city = remember { mutableStateOf(City(id = "", name = "")) }
    )

    // The holder for keeping locations
    val cities = mutableListOf<Prefecture>()

    val weatherApi = WeatherApi(mContext)

    WeatherAppTheme {
        NavHost(navController = navigationController, startDestination = "main") {
            composable(route = "loading") {
                LoadingScreen()
            }
            composable(route = "main") {
                MainScreen(
                    context = mContext,
                    settings = settings,
                    api = weatherApi,
                    onChangeCity = {
                        navigationController.navigateSingleTopTo("settings")
                    }
                )
            }
            composable(route = "settings") {

                SettingsScreen(
                    context = mContext,
                    settings = settings,
                    cities = cities,
                    onClose = {
                        if (settings.city.value.id != "") {
                            CoroutineScope(Dispatchers.IO).launch {
                                settings.save(mContext)
                            }

                            navigationController.navigateUp()
                        }
                    }
                )
            }

            composable(route = "no_internet_error") {
                OfflineScreen()
            }
        }
    }



    runBlocking {

        CoroutineScope(Dispatchers.IO).launch {
            // -------------------------------------------
            //  Load setting values from Database
            // -------------------------------------------
            settings.load(mContext)

            // -------------------------------------------
            //  Load setting values from Database
            // -------------------------------------------
            if (!NetworkUtil.isOnline(mContext)) {
                CoroutineScope(Dispatchers.Main).launch {
                    navigationController.navigate("no_internet_error")
                }
            } else {
                val locationsDeferred = async { weatherApi.getLocationsFromServer() }
                val response = locationsDeferred.await()

                when(response.code()) {
                    200 -> {
                        val result = response.body()!!
                        cities.clear()
                        cities.addAll(result.prefectures)

                        CoroutineScope(Dispatchers.Main).launch {
                            if (settings.city.value.id == "") {
                                navigationController.navigate("settings")
                            } else {
                                navigationController.navigate("main")
                            }
                        }
                    }

                    else -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            navigationController.popupToInclusive("no_internet_error")
                        }
                    }
                }
            }
        }


    }

}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun MainScreen(
    context: Context,
    settings: Settings,
    api: WeatherApi,
    onChangeCity: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppGlobalNav(
            context = context,
            settings = settings,
            menuItems = listOf(
                OptionMenuItem("change_city", "場所の変更")
            ),
            onMenuItemClicked = {
                    menuId ->
                when (menuId) {
                    "change_city" -> onChangeCity()

                    else -> { /* stub */ }
                }
            }
        )

        AppTabs(
            onTabChanged = { tabIndex ->
                when(tabIndex) {
                    0 -> ShowTodayScreen(
                        context = context,
                        settings = settings,
                        api = api
                    )

                    1 -> ShowTomorrowScreen(
                        context = context,
                        api = api
                    )

                    2 -> ShowWeeklyScreen(
                        context = context,
                        api = api
                    )
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
    context: Context,
    settings: Settings,
    cities: MutableCollection<Prefecture>,
    onClose: () -> Unit
) {
    BackHandler(true) {
        onClose()
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SelectCityScreen(context, settings, cities, onClose)
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
fun ShowTodayScreen(context: Context? = null, settings: Settings? = null, api: WeatherApi? = null) {

    TodayWeatherScreen(context, settings, api)
}

@Preview(showBackground = true)
@Composable
fun ShowTomorrowScreen(context: Context? = null, api: WeatherApi? = null) {
    TomorrowWeatherScreen(
        onClick = {
            if (context != null) {
                Toast.makeText(context, "This is tomorrow screen!", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Composable
fun ShowWeeklyScreen(context: Context, api: WeatherApi? = null) {
    WeeklyWeatherScreen(
        onClick = {
            Toast.makeText(context, "This is weekly screen!", Toast.LENGTH_SHORT).show()
        }
    )
}