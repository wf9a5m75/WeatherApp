package com.example.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.AppViewModel
import com.example.weatherapp.network.model.ForecastDay
import com.example.weatherapp.popupToInclusive
import com.example.weatherapp.ui.components.AppGlobalNav
import com.example.weatherapp.ui.components.AppTabs
import com.example.weatherapp.ui.components.OptionMenuItem
import com.example.weatherapp.ui.screens.DailyWeatherScreen
import com.example.weatherapp.ui.screens.LoadingScreen
import com.example.weatherapp.ui.screens.OfflineScreen
import com.example.weatherapp.ui.screens.WeeklyWeatherScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainFragment : Fragment() {
    companion object {

        val Tag: String = MainFragment::class.java.simpleName
    }

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
        NavHost(navController = navigationController, startDestination = "loading") {
            composable(route = "loading") {
                LoadingScreen()
            }
            composable(route = "main") {
                MainScreen(
                    viewModel = viewModel,
                    onChangeCity = {
                    },
                )
            }
            composable(route = "no_internet_error") {
                OfflineScreen()
            }
        }
    }

    // Load the last selected city
    var initTask by remember { mutableStateOf(false) }
    if (!initTask) {
        initTask = true
        viewModel.loadSelectedCity {
            viewModel.syncLocations {
                if (viewModel.city.value.id.isEmpty()) {
                    navigationController.popupToInclusive("settings")
                    return@syncLocations
                }

                viewModel.updateForecasts {
                    navigationController.popupToInclusive("main")
                }
            }
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun MainScreen(
    viewModel: AppViewModel,
    onChangeCity: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        AppGlobalNav(
            viewModel = viewModel,
            menuItems = listOf(
                OptionMenuItem("change_city", "場所の変更"),
            ),
            onMenuItemClicked = { menuId ->
                when (menuId) {
                    "change_city" -> onChangeCity()

                    else -> { /* stub */
                    }
                }
            },
        )

        AppTabs(
            onTabChanged = { tabIndex ->
                when (tabIndex) {
                    0 -> DailyWeatherScreen(ForecastDay.TODAY, viewModel)

                    1 -> DailyWeatherScreen(ForecastDay.TOMORROW, viewModel)

                    2 -> WeeklyWeatherScreen(viewModel)
                }
            },
        )
    }
}
