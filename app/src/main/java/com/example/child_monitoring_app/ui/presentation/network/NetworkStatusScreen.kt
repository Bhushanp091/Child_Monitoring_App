package com.example.child_monitoring_app.ui.presentation.network

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NetworkStatusScreen(networkStatusTracker: NetworkStatusTracker) {
    val isConnected by networkStatusTracker.networkStatus.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
            contentDescription = "Network Status",
            modifier = Modifier.size(64.dp),
            tint = if (isConnected) MaterialTheme.colorScheme.primary else Color.Red
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isConnected) "Connected to the Internet" else "No Internet Connection",
            style = MaterialTheme.typography.titleLarge
        )
    }
}
