package com.example.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.weatherapp.ui.MainFragment
import com.example.weatherapp.ui.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    var mainFragment: MainFragment? = null
    var settingsFragment: SettingsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = MainFragment().also {
                mainFragment = it
            }
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_view, fragment, MainFragment.Tag)
                .commit()
        } else {
            supportFragmentManager.findFragmentByTag(MainFragment.Tag).also {
                if (it is MainFragment) {
                    mainFragment = it
                }
            }
            supportFragmentManager.findFragmentByTag(SettingsFragment.Tag).also {
                if (it is SettingsFragment) {
                    settingsFragment = it
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
