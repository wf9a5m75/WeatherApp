package com.example.weatherapp.model

import android.content.Context
import com.example.weatherapp.di.AppModule
import com.example.weatherapp.mock.MockDispatcher
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.net.HttpURLConnection
import org.mockito.Mockito.`when` as mockWhen

@RunWith(MockitoJUnitRunner::class)
class WeatherApiUnitTest {

    lateinit var context: Context
    lateinit var mockDI: AppModule
    lateinit var cacheDB: CacheDB
    lateinit var cacheDao: CacheDao

    lateinit var server: MockWebServer
    lateinit var api: IWeatherApi
    private val HTTPS_PORT = 443


    @Before
    fun setup() {
        context = mock(Context::class.java)
        mockDI = mock(AppModule::class.java)
        mockWhen(mockDI.provideCacheDB(context)).thenReturn(cacheDB)
        mockWhen(cacheDB.cacheDao()).thenReturn(cacheDao)
        api = mockDI.provideRetrofit(cacheDB)
    }

    @Before
    fun setupMockServer() {
        server= MockWebServer()
        server.start(HTTPS_PORT)
        server.dispatcher = MockDispatcher
    }

    @After
    fun cleanup() {
        server.shutdown()
    }

    @Test
    suspend fun `should obtain correct response`() {
        val response = api.getLocations()

        assertEquals(response.code(), HttpURLConnection.HTTP_OK)
        assertEquals(response.body()?.last_update, "2022-11-01T00:00")
        assertEquals(response.body()?.prefectures?.size, 2)
        assertEquals(response.body()?.prefectures?.get(0)?.id, "osaka")
        assertEquals(response.body()?.prefectures?.get(0)?.cities?.size, 4)
        assertEquals(response.body()?.prefectures?.get(1)?.id, "hyogo")
        assertEquals(response.body()?.prefectures?.get(1)?.cities?.size, 3)

        val etag = response.headers().get("ETag")
        assert(etag != null)
        assertEquals(etag, "etag1")

    }
}
