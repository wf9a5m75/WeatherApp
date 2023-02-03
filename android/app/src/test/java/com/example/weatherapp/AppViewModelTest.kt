package com.example.weatherapp

import com.example.weatherapp.providers.OfflineCaseProviders
import com.example.weatherapp.providers.OnlineCaseProviders
import com.example.weatherapp.network.model.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AppViewModelTest {


    @Before
    fun `create instance`() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }
    @Test
    fun `updateForecasts should obtain data from server if the internet is online`() {
        val viewModel = AppViewModel(
            dispatcher = Dispatchers.Unconfined,
            networkMonitor = OnlineCaseProviders.provideNetworkMonitor(),
            weatherApi = OnlineCaseProviders.provideWeatherApi(),
            prefectureDao = OnlineCaseProviders.providePrefectureDao(),
            keyValueDao = OnlineCaseProviders.provideKeyValueDao(),
            weeklyForecastDao = OnlineCaseProviders.provideWeeklyForecastDao(),
        )
        viewModel.city.value = City("city_a", "somewhere")
        viewModel.updateForecasts {
            assertEquals(2, viewModel.forecasts.size)
            assertEquals("2023-01-30", viewModel.forecasts[0]!!.date)
            assertEquals("2023-01-31", viewModel.forecasts[1]!!.date)
        }
    }
    @Test
    fun `updateForecasts should return the data from database if the device is offline`() {
        val viewModel = AppViewModel(
            dispatcher = Dispatchers.Unconfined,
            networkMonitor = OfflineCaseProviders.provideNetworkMonitor(),
            weatherApi = OfflineCaseProviders.provideWeatherApi(),
            prefectureDao = OfflineCaseProviders.providePrefectureDao(),
            keyValueDao = OfflineCaseProviders.provideKeyValueDao(),
            weeklyForecastDao = OfflineCaseProviders.provideWeeklyForecastDao(),
        )
        viewModel.city.value = City("city_offline", "somewhere")
        viewModel.updateForecasts {
            assertEquals(2, viewModel.forecasts.size)
            assertEquals("2023-02-02", viewModel.forecasts[0]!!.date)
            assertEquals("2023-02-03", viewModel.forecasts[1]!!.date)
        }
    }
    @Test
    fun `updateForecasts should return null if the device is offline and no data is available in database`() {
        val viewModel = AppViewModel(
            dispatcher = Dispatchers.Unconfined,
            networkMonitor = OfflineCaseProviders.provideNetworkMonitor(),
            weatherApi = OfflineCaseProviders.provideWeatherApi(),
            prefectureDao = OfflineCaseProviders.providePrefectureDao(),
            keyValueDao = OfflineCaseProviders.provideKeyValueDao(),
            weeklyForecastDao = OfflineCaseProviders.provideWeeklyForecastDao(),
        )
        viewModel.city.value = City("city_unknown", "somewhere")
        viewModel.updateForecasts {
            assertEquals(true, viewModel.forecasts.isEmpty())
        }
    }

    @Test
    fun `syncLocation should obtain the latest location list if the device is online`() {

        val mockPrefectureDao = OnlineCaseProviders.providePrefectureDao()

        val viewModel = AppViewModel(
            dispatcher = Dispatchers.Unconfined,
            networkMonitor = OnlineCaseProviders.provideNetworkMonitor(),
            weatherApi = OnlineCaseProviders.provideWeatherApi(),
            prefectureDao = mockPrefectureDao,
            keyValueDao = OnlineCaseProviders.provideKeyValueDao(),
            weeklyForecastDao = OnlineCaseProviders.provideWeeklyForecastDao(),
        )
        viewModel.syncLocations {
            assertEquals(2, viewModel.locations.size)
            // TODO: hasBeenCalled?
//            assertEquals()
            assertEquals("pref_a", viewModel.locations[0].id)
            assertEquals("city1_in_pref_a", viewModel.locations[0].cities[0].id)
            assertEquals("city2_in_pref_a", viewModel.locations[0].cities[1].id)
            assertEquals("pref_b", viewModel.locations[1].id)
            assertEquals("city3_in_pref_b", viewModel.locations[1].cities[0].id)
            assertEquals("city4_in_pref_b", viewModel.locations[1].cities[1].id)
        }
    }
}
