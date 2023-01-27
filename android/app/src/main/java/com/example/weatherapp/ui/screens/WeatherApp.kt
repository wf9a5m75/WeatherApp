package com.example.weatherapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.AppViewModel
import com.example.weatherapp.ui.popupToInclusive

// @Preview(showBackground = true)
@Composable
fun WeatherApp(
    viewModel: AppViewModel,
    onLocationMenuClicked: () -> Unit,
    onEmptyLocations: () -> Unit,
) {
    val navigationController = rememberNavController()

    NavHost(navController = navigationController, startDestination = "loading") {
        composable(route = "loading") {
            LoadingScreen()
        }
        composable(route = "main") {
            MainScreen(
                viewModel = viewModel,
                onLocationMenuClicked = onLocationMenuClicked,
            )
        }
        composable(route = "no_internet_error") {
            OfflineScreen()
        }
    }


    // Load the last selected city
    LaunchedEffect(true) {
        viewModel.loadSelectedCity {
            if (viewModel.city.value.id.isEmpty()) {
                viewModel.syncLocations {
                    onEmptyLocations()
                }
                return@loadSelectedCity
            }

            viewModel.updateForecasts {
                navigationController.popupToInclusive("main")
            }
        }
    }
}
