package com.example.weatherapp

import com.example.weatherapp.database.PrefectureDao
import com.example.weatherapp.providers.OfflineCaseDataset
import com.example.weatherapp.providers.OnlineCaseDataset
import com.example.weatherapp.network.model.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class AppViewModelTest {


    @Before
    fun `create instance`() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }
    @Test
    fun `updateForecasts should obtain data from server if the internet is online`() {
        val viewModel = AppViewModel(
            dispatcher = Dispatchers.Unconfined,
            networkMonitor = OnlineCaseDataset.provideNetworkMonitor(),
            weatherApi = OnlineCaseDataset.provideWeatherApi(),
            prefectureDao = OnlineCaseDataset.providePrefectureDao(),
            keyValueDao = OnlineCaseDataset.provideKeyValueDao(),
            weeklyForecastDao = OnlineCaseDataset.provideWeeklyForecastDao(),
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
            networkMonitor = OfflineCaseDataset.provideNetworkMonitor(),
            weatherApi = OfflineCaseDataset.provideWeatherApi(),
            prefectureDao = OfflineCaseDataset.providePrefectureDao(),
            keyValueDao = OfflineCaseDataset.provideKeyValueDao(),
            weeklyForecastDao = OfflineCaseDataset.provideWeeklyForecastDao(),
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
            networkMonitor = OfflineCaseDataset.provideNetworkMonitor(),
            weatherApi = OfflineCaseDataset.provideWeatherApi(),
            prefectureDao = OfflineCaseDataset.providePrefectureDao(),
            keyValueDao = OfflineCaseDataset.provideKeyValueDao(),
            weeklyForecastDao = OfflineCaseDataset.provideWeeklyForecastDao(),
        )
        viewModel.city.value = City("city_unknown", "somewhere")
        viewModel.updateForecasts {
            assertEquals(true, viewModel.forecasts.isEmpty())
        }
    }

    @Test
    fun `syncLocation should obtain the latest location list if the device is online`() {

        val mockPrefectureDao = OnlineCaseDataset.providePrefectureDao()

        val viewModel = AppViewModel(
            dispatcher = Dispatchers.Unconfined,
            networkMonitor = OnlineCaseDataset.provideNetworkMonitor(),
            weatherApi = OnlineCaseDataset.provideWeatherApi(),
            prefectureDao = mockPrefectureDao,
            keyValueDao = OnlineCaseDataset.provideKeyValueDao(),
            weeklyForecastDao = OnlineCaseDataset.provideWeeklyForecastDao(),
        )

        // TODO: spyが取れない
//        mockPrefectureDao as Mockito.spy
        viewModel.syncLocations {
            assertEquals(2, viewModel.locations.size)
//            assertEquals(mockPrefectureDao)
            assertEquals("pref_a", viewModel.locations[0].id)
            assertEquals("city1_in_pref_a", viewModel.locations[0].cities[0].id)
            assertEquals("city2_in_pref_a", viewModel.locations[0].cities[1].id)
            assertEquals("pref_b", viewModel.locations[1].id)
            assertEquals("city3_in_pref_b", viewModel.locations[1].cities[0].id)
            assertEquals("city4_in_pref_b", viewModel.locations[1].cities[1].id)
        }
    }
}
