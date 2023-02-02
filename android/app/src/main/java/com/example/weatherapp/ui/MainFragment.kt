package com.example.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.AppViewModel
import com.example.weatherapp.R
import com.example.weatherapp.ui.screens.WeatherApp
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainFragment : Fragment() {

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
                            findNavController().navigate(R.id.settingsFragment)
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
