package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import com.example.weatherapp.model.Settings
import com.example.weatherapp.ui.AppTabs
import com.example.weatherapp.ui.components.AppGlobalNav
import com.example.weatherapp.ui.components.OptionMenuItem
import com.example.weatherapp.ui.screens.SelectCityScreen
import com.example.weatherapp.ui.screens.TodayWeatherScreen
import com.example.weatherapp.ui.screens.TomorrowWeatherScreen
import com.example.weatherapp.ui.screens.WeeklyWeatherScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme

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
//@Preview(showBackground = true)
//@Composable
//fun WeatherApp2(modifier: Modifier = Modifier) {
//    val navController = rememberNavController()
//
//    NavHost(
//        modifier = Modifier.fillMaxSize(),
//        navController = navController, startDestination = "screen1") {
//        composable(route = "screen1") {
//            val text = "Hello"
//            val id=1234
//            TodayWeatherScreen(
//                onClick = {
//                    navController.navigate("screen2/${text}/${id}")
//                }
//            )
//        }
//        composable(route = "screen2/{text}/{id}",
//            arguments = listOf(
//                navArgument("text") { type = NavType.StringType},
//                navArgument("id") { type = NavType.IntType }
//            )) {
//                backStackEntry ->
//                    val text = backStackEntry.arguments?.getString("text") ?: ""
//                    val id = backStackEntry.arguments?.getInt("id") ?: -1
//
//                    Log.d("test", "text = '${text}'")
//
//                    TomorrowWeatherScreen(
//                        onClick = {
//                            navController.navigateUp()
//                        }
//                    )
//        }
//    }
//}



@OptIn(ExperimentalUnitApi::class)
@Composable
@Preview(showBackground = true)
fun WeatherApp(modifier: Modifier = Modifier) {
    val navigationController = rememberNavController()
    // Hold the context handle
    val mContext = LocalContext.current

    // Keep the values as a Data class
    var settings = Settings(
        city_id = remember { mutableStateOf("hyogo_kobe") }
    )

    WeatherAppTheme {
        NavHost(navController = navigationController, startDestination = "main") {
            composable(route = "main") {
                MainScreen(
                    context = mContext,
                    settings = settings,
                    onChangeCity = {
                        navigationController.navigateSingleTopTo("settings")
                    }
                )
            }
            composable(route = "settings") {
                SettingsScreen(
                    context = mContext,
                    settings = settings,
                    onClick = {
                        navigationController.navigateUp()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun MainScreen(context: Context, settings: Settings, onChangeCity: () -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppGlobalNav(
            context = context,
            settings = settings,
            menuItems = listOf(
                OptionMenuItem("change_city", "場所の変更"),
                OptionMenuItem("change_timezone", "タイムゾーンの変更"),
            ),
            onMenuItemClicked = {
                    menuId ->
                Toast.makeText(context, "${menuId} is clicked", Toast.LENGTH_SHORT).show()

                when (menuId) {
                    "change_city" -> onChangeCity()

                    else -> { /* stub */ }
                }
            }
        )

        AppTabs(
            onTabChanged = {
                tabIndex ->
                    when(tabIndex) {
                        0 -> ShowTodayScreen(context = context)

                        1 -> ShowTomorrowScreen(context = context)

                        2 -> ShowWeeklyScreen(context = context)
                    }
            }
        )

    }
}

@Composable
fun SettingsScreen(context: Context, settings: Settings, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SelectCityScreen(context, settings, onClick)
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


@Composable
fun ShowTodayScreen(context: Context) {
    TodayWeatherScreen()
}

@Composable
fun ShowTomorrowScreen(context: Context) {
    TomorrowWeatherScreen(
        onClick = {
            Toast.makeText(context, "This is tomorrow screen!", Toast.LENGTH_SHORT).show()
        }
    )
}

@Composable
fun ShowWeeklyScreen(context: Context) {
    WeeklyWeatherScreen(
        onClick = {
            Toast.makeText(context, "This is weekly screen!", Toast.LENGTH_SHORT).show()
        }
    )
}