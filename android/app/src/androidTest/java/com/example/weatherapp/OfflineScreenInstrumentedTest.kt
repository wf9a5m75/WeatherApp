package com.example.weatherapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.example.weatherapp.ui.screens.OfflineScreen
import org.junit.Test

class OfflineScreenInstrumentedTest : BaseActivityInstrumentedTest() {
    @Test
    fun should_display_message() {

        composeTestRule.apply {
            setContent {
                OfflineScreen()
            }
        }

        composeTestRule
            .onNodeWithText(getString(R.string.offline_message))
            .assertIsDisplayed()
    }
}
