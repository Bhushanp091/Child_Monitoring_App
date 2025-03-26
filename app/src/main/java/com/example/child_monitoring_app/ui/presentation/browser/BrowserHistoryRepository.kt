package com.example.child_monitoring_app.ui.presentation.browser

class BrowserHistoryRepository {

    suspend fun getBrowserHistory(): List<BrowserHistory> {
        // Simulating fetching history
        return listOf(
            BrowserHistory("https://www.google.com", "2025-03-26 12:30 PM"),
            BrowserHistory("https://www.youtube.com", "2025-03-26 1:00 PM")
        )
    }
}
