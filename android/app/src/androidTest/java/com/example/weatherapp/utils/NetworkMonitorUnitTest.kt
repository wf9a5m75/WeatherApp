package com.example.weatherapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.Mockito.`when` as mockWhen

@RunWith(MockitoJUnitRunner::class)
class NetworkMonitorUnitTest() {
    @Mock
    lateinit var mockConnectivityManager: ConnectivityManager

    @Mock
    lateinit var mockContext: Context

    @Before
    fun setup() {
        // Create a mocked context instance
        mockContext = mock<Context> {}

        // Create a mocked connectivity manager instance
        mockWhen(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
            .thenReturn(mockConnectivityManager)
    }

    @Test
    fun isOnline_should_be_true_when_connected_to_network() {
        val monitor = NetworkMonitor(
            mockConnectivityManager,
            isOnline = true,
        )

        assertTrue(monitor.isOnline)
    }
    @Test
    fun isOnline_should_be_false_when_connected_to_network() {
        val monitor = NetworkMonitor(
            mockConnectivityManager,
            isOnline = false,
        )

        assertFalse(monitor.isOnline)
    }
}
