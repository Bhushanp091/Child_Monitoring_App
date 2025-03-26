package com.example.child_monitoring_app.ui.presentation.browser

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BrowserHistoryScreen(
    viewModel: BrowserHistoryViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val historyList by viewModel.browserHistory.collectAsState()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Browser History", style = MaterialTheme.typography.headlineMedium)

        LazyColumn {
            items(historyList) { history ->
                HistoryItem(url = history.url, timestamp = history.timestamp)
            }
        }
    }
}

@Composable
fun HistoryItem(url: String, timestamp: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = url, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Visited at: $timestamp", style = MaterialTheme.typography.bodySmall)
        }
    }
}
