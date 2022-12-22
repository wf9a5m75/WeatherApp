package com.example.weatherapp

import androidx.activity.viewModels
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelectable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.espresso.Espresso
import com.example.weatherapp.database.KeyValueDao
import com.example.weatherapp.database.PrefectureDao
import com.example.weatherapp.di.AppModule
import com.example.weatherapp.network.IWeatherApi
import com.example.weatherapp.network.model.City
import com.example.weatherapp.network.model.Prefecture
import com.example.weatherapp.ui.screens.SelectCityScreen
import com.example.weatherapp.utils.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import javax.inject.Inject

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
        )
    )

    private val cityLabels = listOf(
        "City1-1", "City1-2", "City1-3", "City2-1", "City2-2", "City3-1", "City3-2",
    )

    lateinit var viewModel: AppViewModel

    @Before
    fun setup() {
//        val networkMonitor = spy(NetworkMonitor::class.java)
//        val weatherAPI = spy(IWeatherApi::class.java)
//        val prefectureDao = spy(PrefectureDao::class.java)
//        val keyValueDao = spy(KeyValueDao::class.java)
//
//        viewModel = AppViewModel(
//            Dispatchers.IO,
//            networkMonitor,
//            weatherAPI,
//            prefectureDao,
//            keyValueDao
//        )
        viewModel = mock(AppViewModel::class.java)
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun shouldDisplayAllCities() {
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
    fun currentCityShouldSelected() {
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
    fun shouldInvokeOnCloseWhenTapOnTheBackButtonOnNavigationBar() {
        val currentCity = City("pref2_city2", "City2-2")
        val targetCity = City("pref3_city2", "City3-2")

        val onCloseCallback = spy(Callback::class.java)
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
    fun shouldInvokeOnCloseWhenTapOnTheBackButtonOfAndroid() {
        val currentCity = City("pref1_city1", "City1-1")
        val targetCity = City("pref3_city1", "City3-1")

        val onCloseCallback = spy(Callback::class.java)
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
