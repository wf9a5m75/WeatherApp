package com.example.weatherapp

import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AppViewModelTest {

//    @Before
//    fun setup() {
//        val mockCacheDao = mock<CacheDao> {
//            on { this.get(any()) } doReturn CacheValue(
//                "https://something",
//                "random_etag",
//                "2022-11-01T00:00"
//            )
//
//            on { this.put(any()) }
//
//            on { this.remove(any()) }
//        }
//
//        val mockWeatherApi = mock<IWeatherApi> {
//            onBlocking { this.getLocations() } doReturn Response.success(
//                LocationResponse(
//                    "2022-11-01T00:00",
//                    listOf(
//                        Prefecture(
//                            "pref1", "Pref1",
//                            listOf(
//                                City("city1", "City1"),
//                            )
//                        )
//                    )
//                )
//            )
//
//            onBlocking { this.getForecast(any(), any()) } doReturn Response.success(
//                ForecastResponse(
//                    "2022-11-01T00:00",
//                    "sunny",
//                    listOf(
//                        Forecast("00:00", 26.0, "Status 1"),
//                    )
//                )
//            )
//        }
//
//    }
//
// //    @Before
// //    fun setupMockServer() {
// //        server= MockWebServer()
// //        server.start(SSL_PORT)
// //        server.dispatcher = MockDispatcher
// //    }
// //
// //    @After
// //    fun cleanup() {
// //        server.shutdown()
// //    }
//
//    @Test
//    suspend fun `should obtain correct response`() {
//        val response = api.getLocations()
//
//        Assert.assertEquals(response.code(), HttpURLConnection.HTTP_OK)
//        Assert.assertEquals(response.body()?.last_update, "2022-11-01T00:00")
//        Assert.assertEquals(response.body()?.prefectures?.size, 2)
//        Assert.assertEquals(response.body()?.prefectures?.get(0)?.id, "osaka")
//        Assert.assertEquals(response.body()?.prefectures?.get(0)?.cities?.size, 4)
//        Assert.assertEquals(response.body()?.prefectures?.get(1)?.id, "hyogo")
//        Assert.assertEquals(response.body()?.prefectures?.get(1)?.cities?.size, 3)
//
//        val etag = response.headers().get("ETag")
//        assert(etag != null)
//        Assert.assertEquals(etag, "etag1")
//
//    }
}
