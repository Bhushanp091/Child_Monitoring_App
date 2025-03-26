package com.example.child_monitoring_app.ui.presentation.appUsage

import android.Manifest
import android.app.Application
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.provider.CallLog
import android.telecom.Call
import android.util.Log
import android.util.LruCache
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.child_monitoring_app.ui.data.FirebaseAuthManager
import com.example.child_monitoring_app.ui.presentation.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale




class AppUsageViewModel(application: Application) : AndroidViewModel(application) {
     val firestoreManager = FirebaseAuthManager()


    var callLogs by mutableStateOf(listOf<CallLogModel>())
        private set


    fun fetchCallLogs(context: Context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val callLogList = mutableListOf<CallLogModel>()
        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC"
        )
        cursor?.use {
            val numberIdx = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIdx = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIdx = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIdx = it.getColumnIndex(CallLog.Calls.DURATION)

            while (it.moveToNext()) {
                val number = it.getString(numberIdx)
                val typeCode = it.getInt(typeIdx)
                val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    .format(Date(it.getLong(dateIdx)))
                val duration = it.getString(durationIdx) + " sec"

                val type = when (typeCode) {
                    CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                    CallLog.Calls.INCOMING_TYPE -> "Incoming"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    else -> "Unknown"
                }
                val callType = when(type){
                    "Outgoing"->CallType.MADE
                    "Incoming"->CallType.RECEIVED
                    "Missed"->CallType.MISSED
                    else->CallType.UNKNOWN
                }

                callLogList.add(CallLogModel("Unknown", number, callType.toString(), date, duration))
            }
        }
        callLogs = callLogList
    }




    private val appUsageService = AppUsageService(application)

    var appUsageData by mutableStateOf<List<AppUsageData>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var currentTimeFrame by mutableStateOf(TimeFrame.DAILY)
        private set

    var hasPermission by mutableStateOf(false)
        private set

    var hasPermissionMain = mutableStateOf(false)
    var callLogsMain = mutableStateOf(emptyList<CallLogModel>())
    val showLoaderMain = mutableStateOf(false)


    init {
        checkPermission()
    }

    fun checkPermission() {
        hasPermission = appUsageService.checkForPermission()
        if (hasPermission) {
            loadAppUsageData()
        }
    }

    fun requestPermission() {
        appUsageService.openUsageAccessSettings()
    }

    fun setTimeFrame(timeFrame: TimeFrame) {
        currentTimeFrame = timeFrame
        loadAppUsageData()
    }

    fun loadAppUsageData() {
        viewModelScope.launch {
            isLoading = true
            appUsageData = appUsageService.getAppUsageData(currentTimeFrame)
            isLoading = false
        }
    }

    fun formatTime(timeInMillis: Long): String {
        return appUsageService.formatTime(timeInMillis)
    }

    fun getTotalScreenTime(): String {
        val totalTime = appUsageData.sumOf { it.usageTime }
        return appUsageService.formatTime(totalTime)
    }




//
//    suspend fun saveCallLogs(userId: String, callLogs: List<CallLogModel>): Result<String> {
//        return withContext(Dispatchers.IO) {
//            firestoreManager.saveCallLogs(userId, callLogs)
//        }
//    }
//
//    suspend fun getCallLogs(userId: String): Result<List<CallLogModel>> {
//        return withContext(Dispatchers.IO) {
//            firestoreManager.getCallLogs(userId)
//        }
//    }


    val showLoader = mutableStateOf(false)

    private val iconCache = LruCache<String, Drawable>(100)



    fun getAppUsageStats(context: Context, startTime: Long, endTime: Long): List<AppUsageInfo> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val packageManager = context.packageManager

        // Track both usage time and last used timestamp
        data class UsageData(var totalTime: Long = 0L, var lastUsed: Long = 0L)

        val appUsageMap = mutableMapOf<String, UsageData>()

        var lastForegroundTime = 0L
        var lastPackageName: String? = null

        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            when (event.eventType) {
                UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                    lastForegroundTime = event.timeStamp
                    lastPackageName = event.packageName
                }

                UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                    if (lastPackageName != null && lastForegroundTime > 0) {
                        val usageTime = event.timeStamp - lastForegroundTime
                        val usageData = appUsageMap.getOrDefault(lastPackageName, UsageData())
                        usageData.totalTime += usageTime
                        usageData.lastUsed = event.timeStamp
                        appUsageMap[lastPackageName!!] = usageData

                        lastForegroundTime = 0L
                        lastPackageName = null
                    }
                }
            }
        }
        return appUsageMap.map { (packageName, usageData) ->
            // First try to get icon from cache
            var icon = iconCache.get(packageName)

            if (icon == null) {
                icon = try {
                    packageManager.getApplicationIcon(packageName).also {
                        // Store in cache for future use
                        iconCache.put(packageName, it)
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    null
                }
            }

            val appName = try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                // Load label using the ApplicationInfo object
                packageManager.getApplicationLabel(appInfo).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                packageName // Fallback to package name
            }

            AppUsageInfo(
                packageName = packageName,
                appName = appName,
                usageTime = usageData.totalTime,
                icon = icon,
                lastTimeUsed = usageData.lastUsed
            )
        }.sortedByDescending { it.lastTimeUsed }
    }

}


data class CallLogModel(
    val name: String,
    val number: String,
    val type: String,
    val date: String,
    val duration: String
)

enum class CallType{
    MISSED,UNKNOWN,MADE,RECEIVED
}


data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val usageTime: Long,
    val icon: Drawable?,
    val lastTimeUsed: Long = 0L
)
