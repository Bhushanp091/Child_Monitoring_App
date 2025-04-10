package com.example.child_monitoring_app.ui.presentation.network

sealed class NetworkStatus {
    object Connected : NetworkStatus()
    object Disconnected : NetworkStatus()
}
