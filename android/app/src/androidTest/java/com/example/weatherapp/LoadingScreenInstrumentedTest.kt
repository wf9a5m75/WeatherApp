package com.example.weatherapp

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.weatherapp.ui.screens.LoadingScreen
import org.junit.Rule
import org.junit.Test

class LoadingScreenInstrumentedTest : BaseActivityInstrumentedTest() {

    @Test
    fun shouldDisplayMessage() {
        composeTestRule.apply {
            setContent {
                LoadingScreen()
            }

            onNodeWithText(getString(R.string.loading_message))
                .assertIsDisplayed()
        }
    }
}
