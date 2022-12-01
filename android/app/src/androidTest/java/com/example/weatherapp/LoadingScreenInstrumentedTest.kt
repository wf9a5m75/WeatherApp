package com.example.weatherapp

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.weatherapp.ui.screens.LoadingScreen
import org.junit.Rule
import org.junit.Test

class LoadingScreenInstrumentedTest {


    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    fun getString(@StringRes resId: Int) = composeTestRule.activity.getString(resId)

    @Test
    fun loadingScreenTest_shouldDisplayMessage() {
        composeTestRule.apply {
            setContent {
                LoadingScreen()
            }

            onNodeWithText(getString(R.string.loading_message))
                .assertIsDisplayed()
        }

    }
}
