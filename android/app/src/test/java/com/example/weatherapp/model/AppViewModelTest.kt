package com.example.weatherapp.model

import com.example.weatherapp.utils.INetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    @Test
    fun testA() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        val networkMonitor = mock<INetworkMonitor> { on { this.isOnline } doReturn true }
        val weatherApi = mock<IWeatherApi> {
            onBlocking { this.getLocations() } doReturn Response.success(
                LocationResponse(
                    "2022-11-01T00:00",
                    listOf(
                        Prefecture(
                            "pref1", "Pref1",
                            listOf(
                                City("city1", "City1"),
                            )
                        )
                    )
                )
            )
            onBlocking { this.getForecast(eq("city1"), any()) } doReturn Response.success(
                ForecastResponse(
                    "2022-11-01T00:00",
                    "sunny",
                    listOf(
                        Forecast("00:00", 26.0, "Status 1"),
                    )
                )
            )
        }
        val prefectureDao = mock<PrefectureDao> {
            onBlocking { this.getAll() } doReturn listOf(
                Prefecture(
                    "pref1", "Pref1",
                    listOf(
                        City("city1", "City1"),
                    )
                )
            )
        }
        val keyValueDao = mock<KeyValueDao> {
        }

        val viewModel = AppViewModel(
            networkMonitor,
            weatherApi,
            prefectureDao,
            keyValueDao,
        )

        val expectedLocations = listOf(
            Prefecture(
                "pref1", "Pref1",
                listOf(
                    City("city1", "City1"),
                )
            )
        )

        assertEquals(0, viewModel.locations.size)

        try {
            viewModel.getLocations { }
        } finally {
            Dispatchers.resetMain()
        }

        assertEquals(expectedLocations.size, viewModel.locations.size)
        assertEquals(expectedLocations[0], viewModel.locations[0])
    }
}
