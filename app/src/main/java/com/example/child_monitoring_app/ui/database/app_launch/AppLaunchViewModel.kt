package com.example.child_monitoring_app.ui.database.app_launch

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AppLaunchViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: AppLaunchRepository

    private val _daily = mutableStateOf<List<AppLaunch>>(emptyList())
    val daily: State<List<AppLaunch>> = _daily

    private val _weekly = mutableStateOf<List<AppLaunch>>(emptyList())
    val weekly: State<List<AppLaunch>> = _weekly

    private val _monthly = mutableStateOf<List<AppLaunch>>(emptyList())
    val monthly: State<List<AppLaunch>> = _monthly

    init {
        val dao = AppDatabase.getDatabase(application).appLaunchDao()
        repo = AppLaunchRepository(dao)
    }

    fun loadStats(date: String, weekStart: String, weekEnd: String, monthStart: String, monthEnd: String) {
        viewModelScope.launch {
            _daily.value = repo.getLaunchesByDate(date)
            _weekly.value = repo.getRange(weekStart, weekEnd)
            _monthly.value = repo.getRange(monthStart, monthEnd)
        }
    }

    fun trackLaunch(packageName: String, date: String) {
        viewModelScope.launch {
            repo.insertOrUpdate(packageName, date)
        }
    }
}
