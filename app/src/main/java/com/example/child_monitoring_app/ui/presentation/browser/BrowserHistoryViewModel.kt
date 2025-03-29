package com.example.child_monitoring_app.ui.presentation.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BrowserHistoryViewModel : ViewModel() {

    private val _browserHistory = MutableStateFlow<List<BrowserHistory>>(emptyList())
    val browserHistory = _browserHistory.asStateFlow()

    fun loadBrowserHistory() {
        viewModelScope.launch {
            _browserHistory.value = BrowserHistoryRepository.getBrowserHistory()
        }
    }
}
