package com.example.weatherapp

import com.example.weatherapp.database.KeyValueDao
import com.example.weatherapp.database.KeyValuePair
import com.example.weatherapp.database.PrefectureDao
import com.example.weatherapp.network.IWeatherApi
import com.example.weatherapp.network.model.City
import com.example.weatherapp.network.model.Forecast
import com.example.weatherapp.network.model.ForecastResponse
import com.example.weatherapp.network.model.LocationResponse
import com.example.weatherapp.network.model.Prefecture
import com.example.weatherapp.utils.INetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.any
import org.mockito.kotlin.anyVararg
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Response
import java.net.HttpURLConnection

class AppViewModelTest {
    private lateinit var viewModel: AppViewModel
    private lateinit var networkMonitor: INetworkMonitor
    private lateinit var weatherApi: IWeatherApi
    private lateinit var prefectureDao: PrefectureDao
    private lateinit var keyValueDao: KeyValueDao

    private val prefectures = listOf<Prefecture>(
        Prefecture(
            "dummyPref1",
            "Pref1",
            listOf(
                City(
                    "city1",
                    "City1"
                ),
                City(
                    "city2",
                    "City2"
                )
            )
        ),

        Prefecture(
            "dummyPref2",
            "Pref2",
            listOf(
                City(
                    "cityA",
                    "CityA"
                ),
                City(
                    "cityB",
                    "CityB"
                )
            )
        )
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun `createViewModel instance`() {
        networkMonitor = mock<INetworkMonitor> {
            on { this.isOnline } doReturn true
        }

        weatherApi = mock<IWeatherApi> {
            onBlocking { getLocations() } doReturn Response.success(
                HttpURLConnection.HTTP_OK,
                LocationResponse("dummy_last_updated", prefectures)
            )

            onBlocking { getForecast("city_a", 0, true) } doReturn Response.success(
                HttpURLConnection.HTTP_OK,
                ForecastResponse(
                    "sometime",
                    "sunny",
                    listOf(
                        Forecast("00:00", 13.0, "sunny"),
                        Forecast("01:00", 12.0, "sunny"),
                        Forecast("02:00", 11.0, "sunny"),
                        Forecast("03:00", 10.0, "sunny")
                    )
                )
            )
        }

//        prefectureDao = spy {
//            onBlocking { this.findByKey("dummyPref1") } doReturn prefectures[0]
//            onBlocking { this.findByKey("dummyPref2") } doReturn prefectures[1]
//            onBlocking { this.count() } doReturn prefectures.size
//            onBlocking { this.getAll() } doReturn prefectures
//        }
        val memo = mutableMapOf<String, Prefecture>()
        prefectureDao = mock {
            onBlocking { this.findByKey(anyString()) } doAnswer {
                val key = it.getArgument(0, String::class.java)
                memo[key]
            }

            onBlocking { this.insertAll(anyVararg()) } doAnswer {
                for (pref in it.arguments[0] as Array<*>) {
                    if (pref!!::class == Prefecture::class) {
                        memo[(pref as Prefecture).id] = pref
                    }
                }
            }

            onBlocking { this.clear() } doAnswer {
                memo.clear()
            }

            onBlocking { this.count() } doReturn memo.size

            onBlocking { this.getAll() } doAnswer {
                memo.values.toList<Prefecture>()
            }
        }

        val memo2 = mutableMapOf<String, KeyValuePair>()
        keyValueDao = mock {
            onBlocking { this.put(any<KeyValuePair>()) } doAnswer {
                val keyValue = it.arguments[0] as KeyValuePair
                memo2[keyValue.id] = keyValue
            }
            onBlocking { this.get(anyString()) } doAnswer {
                val key = it.getArgument<String>(0)
                memo2[key]
            }
        }

        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        viewModel = AppViewModel(
            Dispatchers.Main,
            networkMonitor = networkMonitor,
            weatherApi = weatherApi,
            prefectureDao = prefectureDao,
            keyValueDao = keyValueDao
        )
    }

    @Test
    fun `syncLocations() should update the locations property`() {
        viewModel.syncLocations {
            assertEquals(prefectures.size, viewModel.locations.size)
            assertEquals(prefectures[0], viewModel.locations[0])
        }
    }

    @Test
    fun `updateTodayForecast() should update the todayForecast property`() {
        viewModel.city.value = City("city_a", "somewhere")
        viewModel.updateTodayForecast {
            assertEquals(true, it)
            assertEquals("sometime", viewModel.todayForecast.value?.last_update)
            assertEquals(4, viewModel.todayForecast.value?.forecasts?.size)
            assertEquals("03:00", viewModel.todayForecast.value?.forecasts?.get(3)?.time)
        }
    }
}
