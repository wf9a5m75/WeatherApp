package com.example.weatherapp.utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import javax.inject.Inject

interface INetworkMonitor {
    var isOnline: Boolean
}

class NetworkMonitor @Inject constructor(
    connectivityManager: ConnectivityManager,
    override var isOnline: Boolean = false,
) : INetworkMonitor, ConnectivityManager.NetworkCallback() {
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
