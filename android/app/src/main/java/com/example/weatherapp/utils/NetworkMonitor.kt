package com.example.weatherapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class NetworkMonitor(
    context: Context
) : ConnectivityManager.NetworkCallback() {
    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val connectivityManager: ConnectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager

    var isOnline by mutableStateOf(false)

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        this.isOnline = true
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        this.isOnline = false
    }

    init {
        this.connectivityManager.registerNetworkCallback(networkRequest, this)
    }
}
