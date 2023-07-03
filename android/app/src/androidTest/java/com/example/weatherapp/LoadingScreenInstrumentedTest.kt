package com.example.weatherapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.example.weatherapp.ui.screens.LoadingScreen
import org.junit.Test

class LoadingScreenInstrumentedTest : BaseActivityInstrumentedTest() {

    @Test
    fun should_display_message() {
        composeTestRule.apply {
            setContent {
                LoadingScreen()
            }

            onNodeWithText(getString(R.string.loading_message))
                .assertIsDisplayed()
        }
    }
}
