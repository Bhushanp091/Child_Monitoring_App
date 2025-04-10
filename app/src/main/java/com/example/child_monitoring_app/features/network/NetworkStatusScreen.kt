package com.example.child_monitoring_app.features.network

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.child_monitoring_app.R

@Composable
fun NetworkStatusScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val networkStatusTracker = NetworkStatusTracker(context)
    val isConnected by networkStatusTracker.networkStatus.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter =  painterResource( if (isConnected) R.drawable.baseline_wifi_24 else R.drawable.baseline_wifi_off_24),
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
