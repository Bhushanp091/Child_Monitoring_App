package com.example.child_monitoring_app.ui.presentation.browser

object BrowserHistoryRepository {

    private val historyList = mutableListOf<BrowserHistory>()

    fun addBrowserHistory(url: String, timestamp: String) {
        historyList.add(BrowserHistory(url, timestamp))
    }

    suspend fun getBrowserHistory(): List<BrowserHistory> {
        return historyList
    }
}
