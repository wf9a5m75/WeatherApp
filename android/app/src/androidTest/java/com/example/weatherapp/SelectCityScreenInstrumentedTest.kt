package com.example.weatherapp

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelectable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.weatherapp.model.City
import com.example.weatherapp.model.Prefecture
import com.example.weatherapp.ui.screens.SelectCityScreen
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class SelectCityScreenInstrumentedTest : BaseActivityInstrumentedTest() {

    private val prefectures = listOf(
        Prefecture(
            "prefecture1",
            "Prefecture1",
            listOf(
                City("pref1_city1", "City1-1"),
                City("pref1_city2", "City1-2"),
                City("pref1_city3", "City1-3")
            )
        ),
        Prefecture(
            "prefecture2",
            "Prefecture2",
            listOf(
                City("pref2_city1", "City2-1"),
                City("pref2_city2", "City2-2")
            )
        ),
        Prefecture(
            "prefecture3",
            "Prefecture3",
            listOf(
                City("pref3_city1", "City3-1"),
                City("pref3_city2", "City3-2")
            )
        )
    )

    private val cityLabels = listOf(
        "City1-1", "City1-2", "City1-3", "City2-1", "City2-2", "City3-1", "City3-2"
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun shouldDisplayAllCities() {
        composeTestRule.apply {
            setContent {
                SelectCityScreen(
                    locations = prefectures
                )
            }

            for (label in cityLabels) {
                composeTestRule
                    .onNodeWithText(label)
                    .assertIsDisplayed()
                    .assertIsSelectable()
            }
        }
    }

    @Test
    fun currentCityShouldSelected() {
        val currentCity = City("pref2_city2", "City2-2")
        composeTestRule.apply {
            setContent {
                SelectCityScreen(
                    locations = prefectures,
                    currentCity = currentCity
                )
            }
            composeTestRule
                .onNodeWithText(currentCity.name)
                .assertIsSelected()
        }
    }

    private interface Callback : (City) -> Unit

    @Test
    fun shouldInvokeOnCloseWhenTapOnTheBackButton() {
        val currentCity = City("pref2_city2", "City2-2")
        val targetCity = City("pref3_city2", "City3-2")

        var selectedCity = mutableStateOf(currentCity)
        val onCloseCallback = mock(Callback::class.java)

        composeTestRule.apply {
            setContent {
                SelectCityScreen(
                    locations = prefectures,
                    currentCity = currentCity,
                    onClose = {
                        selectedCity.value = it
                    }
                )
            }
            composeTestRule
                .onNodeWithText(targetCity.name)
                .performClick()
                .assertIsSelected()

            composeTestRule
                .onNodeWithContentDescription("Back")
                .performClick()

            assert(selectedCity.value.id == targetCity.id)
//            verify(onCloseCallback, times(2))
        }
    }
}
