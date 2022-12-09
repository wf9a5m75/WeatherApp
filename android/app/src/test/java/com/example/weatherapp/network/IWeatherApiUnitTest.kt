package com.example.weatherapp.network

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weatherapp.mock.MockDispatcher
import com.example.weatherapp.network.cache.CacheDB
import com.example.weatherapp.network.cache.CacheDao
import com.example.weatherapp.network.cache.CacheValue
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class IWeatherApiUnitTest {

    lateinit var server: MockWebServer
    var api: IWeatherApi? = null
    private val SSL_PORT = 443
    private lateinit var db: CacheDB
    private lateinit var dao: CacheDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CacheDB::class.java).build()
        dao = db.cacheDao()
        api = RetrofitHelper.getInstance(dao).create(IWeatherApi::class.java)

    }

    @Before
    fun setupMockServer() {
        server= MockWebServer()
        server.start(SSL_PORT)
        server.dispatcher = MockDispatcher
    }

    @After
    fun cleanup() {
        server.shutdown()
        db.close()
    }

    @Test
    fun `test cacheDao for get, put and remove methods`() {
        val expectVal = CacheValue(
            "urlSomewhere",
            "eTagSomething",
            "lastModifiedSomething"
        )
        dao.put(expectVal)

        val readValue = dao.get("urlSomewhere")
        assertEquals("urlSomething", readValue?.url)
        assertEquals("eTagSomething", readValue?.eTag)
        assertEquals("lastModifiedSomething", readValue?.lastModified)

        dao.remove("urlSomething")
        val afterReadValue = dao.get("urlSomething")
        assertEquals(null, afterReadValue)

//        val response = api.getLocations()
//
//        assertEquals(response.code(), HttpURLConnection.HTTP_OK)
//        assertEquals(response.body()?.last_update, "2022-11-01T00:00")
//        assertEquals(response.body()?.prefectures?.size, 2)
//        assertEquals(response.body()?.prefectures?.get(0)?.id, "osaka")
//        assertEquals(response.body()?.prefectures?.get(0)?.cities?.size, 4)
//        assertEquals(response.body()?.prefectures?.get(1)?.id, "hyogo")
//        assertEquals(response.body()?.prefectures?.get(1)?.cities?.size, 3)
//
//        val etag = response.headers().get("ETag")
//        assert(etag != null)
//        assertEquals(etag, "etag1")

    }
}
