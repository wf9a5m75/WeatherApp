package com.example.weatherapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelectable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import com.example.weatherapp.database.KeyValueDao
import com.example.weatherapp.database.PrefectureDao
import com.example.weatherapp.database.WeeklyForecastDao
import com.example.weatherapp.network.IWeatherApi
import com.example.weatherapp.network.model.City
import com.example.weatherapp.network.model.LocationResponse
import com.example.weatherapp.network.model.Prefecture
import com.example.weatherapp.ui.screens.SelectCityScreen
import com.example.weatherapp.utils.INetworkMonitor
import kotlinx.coroutines.Dispatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy
import retrofit2.Response

class SelectCityScreenInstrumentedTest : BaseActivityInstrumentedTest() {

    private val prefectures = listOf(
        Prefecture(
            "prefecture1",
            "Prefecture1",
            listOf(
                City("pref1_city1", "City1-1"),
                City("pref1_city2", "City1-2"),
                City("pref1_city3", "City1-3"),
            ),
        ),
        Prefecture(
            "prefecture2",
            "Prefecture2",
            listOf(
                City("pref2_city1", "City2-1"),
                City("pref2_city2", "City2-2"),
            ),
        ),
        Prefecture(
            "prefecture3",
            "Prefecture3",
            listOf(
                City("pref3_city1", "City3-1"),
                City("pref3_city2", "City3-2"),
            ),
        ),
    )

    private val cityLabels = listOf(
        "City1-1", "City1-2", "City1-3", "City2-1", "City2-2", "City3-1", "City3-2",
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    private fun createViewModel(): AppViewModel {
        val networkMonitor = spy<INetworkMonitor> {
            on { isOnline } doReturn true
        }

        val weatherAPI = spy<IWeatherApi> {
            onBlocking { this.getLocations() } doReturn Response.success(
                LocationResponse(
                    "2022-11-15T22:33",
                    prefectures,
                ),
            )
        }
        val prefectureDao = spy<PrefectureDao> {
            onBlocking { this.getAll() } doReturn prefectures
        }
        val keyValueDao = spy<KeyValueDao> { }

        val weeklyForecastDao = spy<WeeklyForecastDao> {}

        return AppViewModel(
            Dispatchers.IO,
            networkMonitor,
            weatherAPI,
            prefectureDao,
            keyValueDao,
            weeklyForecastDao,
        )
    }

    @Test
    fun should_display_all_cities() {
        val viewModel = this.createViewModel()

        viewModel.locations.addAll(prefectures)
        composeTestRule.apply {
            setContent {
                SelectCityScreen(
                    viewModel = viewModel,
                ) { }
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
    fun current_city_should_selected() {
        val viewModel = this.createViewModel()
        val currentCity = City("pref2_city2", "City2-2")
        viewModel.city.value = currentCity
        composeTestRule.apply {
            setContent {
                SelectCityScreen(
                    viewModel = viewModel,
                )
            }
            composeTestRule
                .onNodeWithText(currentCity.name)
                .assertIsSelected()
        }
    }

    private interface Callback : () -> Unit

    @Test
    fun should_invoke_onClose_when_tap_on_the_backButton_on_navigationBar() {
        val currentCity = City("pref2_city2", "City2-2")
        val targetCity = City("pref3_city2", "City3-2")

        val viewModel = this.createViewModel()
        val onCloseCallback = spy<Callback> {}
        viewModel.city.value = currentCity
        viewModel.locations.addAll(prefectures)

        composeTestRule.apply {
            setContent {
                SelectCityScreen(
                    viewModel = viewModel,
                ) {
                    onCloseCallback()
                }
            }
            composeTestRule
                .onNodeWithText(targetCity.name)
                .performClick()
                .assertIsSelected()

            composeTestRule
                .onNodeWithContentDescription("Back")
                .performClick()

            verify(onCloseCallback, times(1)).invoke()
            Assert.assertEquals(targetCity, viewModel.city.value)
        }
    }

    @Test
    fun should_invoke_onClose_when_tap_on_the_backButton_of_Android() {
        val currentCity = City("pref1_city1", "City1-1")
        val targetCity = City("pref3_city1", "City3-1")

        val viewModel = this.createViewModel()
        val onCloseCallback = spy<Callback> {}
        viewModel.city.value = currentCity
        viewModel.locations.addAll(prefectures)

        composeTestRule.apply {
            setContent {
                SelectCityScreen(
                    viewModel = viewModel,
                    onClose = onCloseCallback,
                )
            }
            composeTestRule
                .onNodeWithText(targetCity.name)
                .performClick()
                .assertIsSelected()

            Espresso.pressBack()

            verify(onCloseCallback, times(1)).invoke()
        }
    }
}
