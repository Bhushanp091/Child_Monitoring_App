package com.example.child_monitoring_app.ui.presentation.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BrowserHistory(val url: String, val timestamp: String)

class BrowserHistoryViewModel(
    private val repository: BrowserHistoryRepository = BrowserHistoryRepository()
) : ViewModel() {

    private val _browserHistory = MutableStateFlow<List<BrowserHistory>>(emptyList())
    val browserHistory = _browserHistory.asStateFlow()

    init {
        loadBrowserHistory()
    }

    private fun loadBrowserHistory() {
        viewModelScope.launch {
            val history = repository.getBrowserHistory()
            _browserHistory.value = history
        }
    }
}
