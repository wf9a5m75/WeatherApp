package com.example.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.AppViewModel
import com.example.weatherapp.R
import com.example.weatherapp.ui.screens.SelectCityScreen

class SettingsFragment : Fragment() {

    val viewModel by activityViewModels<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view as ComposeView

        val navController = findNavController()
        view.setContent {
            SelectCityScreen(
                viewModel = viewModel,
            ) {
                // If no preference, move to the selectCity screen
                viewModel.saveSelectedCity {
                    // move back to the main fragment
                    navController.popBackStack(R.id.mainFragment, false)
//                    if (navController.backQueue.isNotEmpty()) {
//                        navController.popBackStack()
//                    } else {
//                        navController.navigate(R.id.mainFragment)
//                    }
                }
            }
        }
    }
}
