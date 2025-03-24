package com.example.child_monitoring_app.ui.presentation.appUsage

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CallLog
import android.telecom.Call
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
    private val firestoreManager = FirebaseAuthManager()


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

                callLogList.add(CallLogModel("Unknown", number, callType, date, duration))
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


}


data class CallLogModel(
    val name: String,
    val number: String,
    val type: CallType,
    val date: String,
    val duration: String
)

enum class CallType{
    MISSED,UNKNOWN,MADE,RECEIVED
}
