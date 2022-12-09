package com.example.weatherapp.network.cache

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CacheDbTest {

    private lateinit var db: CacheDB
    private lateinit var dao: CacheDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CacheDB::class.java).build()
        dao = db.cacheDao()
    }

    @After
    fun cleanup() {
        db.close()
    }

    @Test
    fun get_put_remove_test() {
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
    }
}
