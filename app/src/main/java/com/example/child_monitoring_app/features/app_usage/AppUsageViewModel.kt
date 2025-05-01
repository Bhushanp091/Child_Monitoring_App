package com.example.child_monitoring_app.features.app_usage

import android.app.usage.UsageStatsManager
import androidx.compose.runtime.mutableStateOf
import com.example.child_monitoring_app.core.ui.BaseViewModel
import com.example.child_monitoring_app.features.call_log_history.Contact
import java.util.Calendar


class AppUsageViewModel : BaseViewModel() {

//    var usageData = mutableStateOf<List<AppUsageInfo>>(emptyList())
    var blockedApp = mutableStateOf<List<AppUsageInfo>>(emptyList())
    var callLogsMain = mutableStateOf(emptyList<CallLogModel>())
    var contactList = mutableStateOf(emptyList<Contact>())
//    var selectedInterval = mutableStateOf(UsageStatsManager.INTERVAL_DAILY)
    val flag = mutableStateOf(true)
    val callLogFlag =  mutableStateOf(true)



    val selectedInterval = mutableStateOf(IntervalType.DAILY)
    val usageData = mutableStateOf<List<AppUsageInfo>>(emptyList())

    // Helper enum for clearer interval type handling
    enum class IntervalType(val calendarField: Int, val apiName: String) {
        DAILY(Calendar.DAY_OF_MONTH, "daily"),
        WEEKLY(Calendar.WEEK_OF_YEAR, "weekly"),
        MONTHLY(Calendar.MONTH, "monthly")
    }

    fun fetchAppUsageData(parentId: String) {
        val intervalType = selectedInterval.value.apiName

        firebaseManager.fetchAppUsageFromFirebase(
            parentId = parentId,
            childId = childId.value,
            intervalType = intervalType
        ) { usageList ->
            usageData.value = usageList
        }
    }

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
    val packageName: String = "",
    val appName: String = "",
    val usageTime: String = "", // formatted as HH:MM:SS for display
    val usageTimeMillis: String = "0", // raw milliseconds for sorting
    val lastTimeUsed: String = "", // formatted as readable date
    val icon: String = "" // Base64 encoded icon (optional)
)
