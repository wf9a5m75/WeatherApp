package com.example.weatherapp.utils

import android.content.Context
import android.net.ConnectivityManager
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class NetworkMonitorUnitTest() {
    @Mock
    lateinit var mockConnectivityManager: ConnectivityManager
    @Mock
    lateinit var mockContext: Context

    @Before
    fun setup() {
        // Create a mocked context instance
//        mockContext = mock {  }
//        mockContext = mock(Context::class.java)
//
//        // Create a mocked connectivity manager instance
//        org.mockito.kotlin.mock<> {  }(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
//            .thenReturn(mockConnectivityManager)
//
    }

    @Test
    fun `isOnline should be true when connected to network`() {
        val monitor = NetworkMonitor(mockConnectivityManager)



        assertTrue(monitor.isOnline)
    }
}
