package com.example.weatherapp

import com.example.weatherapp.database.KeyValueDao
import com.example.weatherapp.database.KeyValuePair
import com.example.weatherapp.database.PrefectureDao
import com.example.weatherapp.di.AppModule
import com.example.weatherapp.network.IWeatherApi
import com.example.weatherapp.network.cache.ETagInspector
import com.example.weatherapp.network.model.City
import com.example.weatherapp.network.model.Prefecture
import com.example.weatherapp.utils.INetworkMonitor
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy

class AppViewModelTest {
    private lateinit var viewModel: AppViewModel
    private lateinit var networkMonitor: INetworkMonitor
    private lateinit var weatherApi: IWeatherApi
    private lateinit var prefectureDao: PrefectureDao
    private lateinit var keyValueDao: KeyValueDao
    private val server: MockWebServer = MockWebServer()

    private val prefectures = listOf<Prefecture>(
        Prefecture(
            "dummyPref1",
            "Pref1",
            listOf(
                City(
                    "city1",
                    "City1",
                ),
                City(
                    "city2",
                    "City2",
                ),
            ),
        ),

        Prefecture(
            "dummyPref2",
            "Pref2",
            listOf(
                City(
                    "cityA",
                    "CityA",
                ),
                City(
                    "cityB",
                    "CityB",
                ),
            ),
        ),
    )

    @Before
    fun `start mock webserver`() {
        val server = MockWebServer()
        val response = MockResponse()
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("X-CUSTOM-MESSAGE", "This response made by mock server")
            .setBody("""
{
  "last_update": "2023-01-19T20:56",
  "overall": "sunny",
  "forecasts": [
    {
      "date": "2023-01-20",
      "forecasts": [
        {
          "time": "2023-01-20T00:00",
          "status": "snow",
          "temperature": -3.6
        },
        {
          "status": "snow",
          "temperature": -2.7,
          "time": "2023-01-20T01:00"
        },
        {
          "status": "snow",
          "temperature": -2,
          "time": "2023-01-20T02:00"
        }
      ]
    }
  ]
}
            """.trimIndent())
        server.enqueue(response);
        server.start()

        networkMonitor = mock<INetworkMonitor> {
            on { this.isOnline } doReturn true
        }

//        val mockLogger = mock<HttpLoggingInterceptor>() {
//            on { this.level } doReturn HttpLoggingInterceptor.Level.NONE
//        }
//
//        val mockEtagInspector = mock<ETagInspector>() {
//            onBlocking { this.intercept(any(Interceptor.Chain::class.java)) } doAnswer  {
//                val chain = it.getArgument<Interceptor.Chain>(0)
//                var request = chain.request()
//                chain.proceed(request)
//            }
//        }

        val httpClient = AppModule.provideHttpClient(listOf())
        weatherApi = AppModule.provideWeatherApi(
            apiEntryPoint = "http://localhost",
            httpClient = httpClient,
        )

        val mockPrefectureDao = mock<PrefectureDao> {
            onBlocking { this.findByKey(anyString()) } doReturn null

            onBlocking { this.count() } doReturn 0

            onBlocking { this.getAll() } doReturn listOf<Prefecture>()

            onBlocking { this.clear() } doAnswer { }

            onBlocking { this.insertAll(any(Prefecture::class.java)) } doAnswer { }
        }

        val mockKeyValueDao = mock<KeyValueDao> {
            onBlocking { this.get(anyString()) } doReturn null

            onBlocking { this.put(any(KeyValuePair::class.java)) }
        }

        viewModel = AppViewModel(
            dispatcher = Dispatchers.Main,
            networkMonitor = networkMonitor,
            weatherApi = weatherApi,
            prefectureDao = mockPrefectureDao,
            keyValueDao = mockKeyValueDao,
        )
    }

    @After
    fun `shutdown the mock server`() {
        server.shutdown()
    }
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Before
//    fun `createViewModel instance`() {
//        networkMonitor = mock<INetworkMonitor> {
//            on { this.isOnline } doReturn true
//        }
//
//        weatherApi = mock<IWeatherApi> {
//            onBlocking { getLocations() } doReturn Response.success(
//                HttpURLConnection.HTTP_OK,
//                LocationResponse("dummy_last_updated", prefectures),
//            )
//
//            onBlocking {
//                getForecast(
//                    eq("city_a"),
//                    eq(ForecastDay.TODAY.day),
//                    anyBoolean(),
//                )
//            } doReturn
//                Response.success(
//                    HttpURLConnection.HTTP_OK,
//                    ForecastResponse(
//                        "sometime_today",
//                        "sunny",
//                        listOf(
//                            Forecast("00:00", 13.0, "sunny"),
//                            Forecast("01:00", 12.0, "sunny"),
//                            Forecast("02:00", 11.0, "sunny"),
//                            Forecast("03:00", 10.0, "sunny"),
//                        ),
//                    ),
//                )
//
//            onBlocking {
//                getForecast(
//                    eq("city_b"),
//                    eq(ForecastDay.TOMORROW.day),
//                    anyBoolean(),
//                )
//            } doReturn
//                Response.success(
//                    HttpURLConnection.HTTP_OK,
//                    ForecastResponse(
//                        "sometime_tomorrow",
//                        "cloudy",
//                        listOf(
//                            Forecast("00:00", 8.0, "cloudy"),
//                            Forecast("01:00", 7.0, "cloudy"),
//                            Forecast("02:00", 7.0, "cloudy"),
//                            Forecast("03:00", 6.0, "cloudy"),
//                        ),
//                    ),
//                )
//        }
//
//        val memo = mutableMapOf<String, Prefecture>()
//        prefectureDao = mock {
//            onBlocking { this.findByKey(anyString()) } doAnswer {
//                val key = it.getArgument(0, String::class.java)
//                memo[key]
//            }
//
//            onBlocking { this.insertAll(anyVararg()) } doAnswer {
//                for (pref in it.arguments[0] as Array<*>) {
//                    if (pref!!::class == Prefecture::class) {
//                        memo[(pref as Prefecture).id] = pref
//                    }
//                }
//            }
//
//            onBlocking { this.clear() } doAnswer {
//                memo.clear()
//            }
//
//            onBlocking { this.count() } doReturn memo.size
//
//            onBlocking { this.getAll() } doAnswer {
//                memo.values.toList<Prefecture>()
//            }
//        }
//
//        val memo2 = mutableMapOf<String, KeyValuePair>()
//        keyValueDao = mock {
//            onBlocking { this.put(any<KeyValuePair>()) } doAnswer {
//                val keyValue = it.arguments[0] as KeyValuePair
//                memo2[keyValue.id] = keyValue
//            }
//            onBlocking { this.get(anyString()) } doAnswer {
//                val key = it.getArgument<String>(0)
//                memo2[key]
//            }
//        }
//
//        val testDispatcher = UnconfinedTestDispatcher()
//        Dispatchers.setMain(testDispatcher)
//
//        viewModel = AppViewModel(
//            Dispatchers.Main,
//            networkMonitor = networkMonitor,
//            weatherApi = weatherApi,
//            prefectureDao = prefectureDao,
//            keyValueDao = keyValueDao,
//        )
//    }
//
//    @Test
//    fun `syncLocations() should update the locations property`() {
//        viewModel.syncLocations {
//            assertEquals(prefectures.size, viewModel.locations.size)
//            assertEquals(prefectures[0], viewModel.locations[0])
//        }
//    }
//
    @Test
    fun `obtain the weekly forecast correctly`() {
        viewModel.city.value = City("city_a", "somewhere")
        viewModel.updateForecasts {
            assertEquals(1, viewModel.forecasts.size)
            assertEquals("2023-01-20", viewModel.forecasts[0]!!.date)
        }
    }
//
//    @Test
//    fun `updateForecast(tomorrow) should obtain the tomorrow forecast`() {
//        viewModel.city.value = City("city_b", "somewhere")
//        viewModel.updateForecast(
//            ForecastDay.TOMORROW,
//        ) {
//            assertEquals(true, it)
//            assertEquals("sometime_tomorrow", viewModel.forecasts[1]?.last_update)
//            assertEquals(4, viewModel.forecasts[1]?.forecasts?.size)
//            assertEquals(6.0, viewModel.forecasts[1]?.forecasts?.get(3)?.temperature)
//        }
//    }
}
