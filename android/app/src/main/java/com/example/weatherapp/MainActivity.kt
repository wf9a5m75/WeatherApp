package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherapp.model.Settings
import com.example.weatherapp.ui.AppTabs
import com.example.weatherapp.ui.components.AppGlobalNav
import com.example.weatherapp.ui.components.OptionMenuItem
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
    val mContext = LocalContext.current
    var count by remember { mutableStateOf(0) }
    var settings = Settings(
        city_id = remember { mutableStateOf("hyogo_kobe") }
    )

    WeatherAppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AppGlobalNav(
                context = mContext,
                settings = settings,
                menuItems = listOf(
                    OptionMenuItem("change_city", "場所の変更"),
                    OptionMenuItem("change_timezone", "タイムゾーンの変更"),
                ),
                onMenuItemClicked = {
                    menuId ->
                        Toast.makeText(mContext, "${menuId} is clicked", Toast.LENGTH_SHORT).show()

                        when (menuId) {
                            "change_city" -> {
                                count = (count + 1) % 3
                                settings.city_id.value = when(count) {
                                    0 -> "hyogo_kobe"
                                    1 -> "osaka_osaka"
                                    else -> "somewhere"
                                }
                                Log.d("Test", "selected menu is ${menuId}")
                            }

                            else -> { /* stub */ }
                        }
                }
            )

            AppTabs(
                context = mContext
            )

        }
    }
}
