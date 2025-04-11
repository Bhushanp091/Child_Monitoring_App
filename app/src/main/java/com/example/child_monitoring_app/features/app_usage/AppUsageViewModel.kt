package com.example.child_monitoring_app.features.app_usage

import android.app.usage.UsageStatsManager
import androidx.compose.runtime.mutableStateOf
import com.example.child_monitoring_app.core.ui.BaseViewModel
import com.example.child_monitoring_app.features.call_log_history.Contact


class AppUsageViewModel : BaseViewModel() {

    var usageData = mutableStateOf<List<AppUsageInfo>>(emptyList())
    var callLogsMain = mutableStateOf(emptyList<CallLogModel>())
    var contactList = mutableStateOf(emptyList<Contact>())
    var selectedInterval = mutableStateOf(UsageStatsManager.INTERVAL_DAILY)
    val flag = mutableStateOf(true)
    val callLogFlag =  mutableStateOf(true)

}


data class CallLogModel(
    val name: String,
    val number: String,
    val type: String,
    val date: String,
    val duration: String
)

enum class CallType {
    MISSED, UNKNOWN, MADE, RECEIVED
}


data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val usageTime: String,
    val icon: String?,
    val lastTimeUsed: String = ""
)
