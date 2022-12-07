package com.example.weatherapp.utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import javax.inject.Inject

class NetworkMonitor @Inject constructor(
    connectivityManager: ConnectivityManager
) : ConnectivityManager.NetworkCallback() {

    var isOnline = false

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        this.isOnline = true
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        this.isOnline = false
    }

    init {
        val networkRequest: NetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }
}
