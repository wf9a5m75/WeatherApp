package com.example.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.AppViewModel
import com.example.weatherapp.R
import com.example.weatherapp.ui.screens.LoadingScreen
import com.example.weatherapp.ui.screens.MainScreen
import com.example.weatherapp.ui.screens.OfflineScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainFragment: Fragment() {

    val viewModel by activityViewModels<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view as ComposeView
        view.setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    // modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    WeatherApp(
                        viewModel,
                        onLocationMenuClicked = {
                            findNavController().navigate(R.id.mainFragment)
                        },
                        onEmptyLocations = {
                            findNavController().navigate(R.id.settingsFragment)
                        }
                    )
                }
            }
        }
    }
}


// @Preview(showBackground = true)
@Composable
fun WeatherApp(
    viewModel: AppViewModel,
    onLocationMenuClicked: () -> Unit,
    onEmptyLocations: () -> Unit,
) {
    val navigationController = rememberNavController()
    var hasBeenInitialized by remember { mutableStateOf(false) }

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
    if (!hasBeenInitialized) {
        hasBeenInitialized = true
        viewModel.loadSelectedCity {
            viewModel.syncLocations {
                if (viewModel.city.value.id.isEmpty()) {
                    onEmptyLocations()
                    return@syncLocations
                }

                viewModel.updateForecasts {
                    navigationController.popupToInclusive("main")
                }
            }
        }
    }
}


fun NavHostController.popupToInclusive(route: String) = this.navigate(route) {
    // Pop up to the start destination of the graph
    // to avoid building up a large stack of destinations
    // on the back stack as users select items
    popUpTo(
        this@popupToInclusive.graph.findStartDestination().id,
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
