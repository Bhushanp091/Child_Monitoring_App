package com.example.child_monitoring_app.ui.presentation.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkStatusTracker(context: Context) {
    private val _networkStatus = MutableStateFlow(false)
    val networkStatus: StateFlow<Boolean> = _networkStatus

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    init {
        val builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _networkStatus.value = true
                }

                override fun onLost(network: Network) {
                    _networkStatus.value = false
                }
            }
        )
        checkInitialConnection()
    }

    private fun checkInitialConnection() {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        _networkStatus.value = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
