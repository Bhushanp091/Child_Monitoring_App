package com.example.child_monitoring_app.features.call_log_history

import android.content.Context
import android.provider.CallLog
import android.util.Log
import com.example.child_monitoring_app.features.app_usage.CallLogModel
import com.example.child_monitoring_app.features.app_usage.CallType
import java.util.Date

fun getCallLogs(context: Context): List<CallLogModel> {
    val callLogs = mutableListOf<CallLogModel>()

    try {
        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC"
        )

        cursor?.use {
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
            val name = it.getColumnIndex(CallLog.Calls.CACHED_NAME)

            while (it.moveToNext()) {
                val phoneNumber = it.getString(numberIndex)
                val callType = it.getInt(typeIndex)
                val callDate = it.getLong(dateIndex)
                val callDuration = it.getString(durationIndex)
                val userName = it.getString(name)

                val callDayTime = Date(callDate)
                val callDirection = when (callType) {
                    CallLog.Calls.OUTGOING_TYPE -> CallType.MADE
                    CallLog.Calls.INCOMING_TYPE -> CallType.RECEIVED
                    CallLog.Calls.MISSED_TYPE -> CallType.MISSED
                    else -> CallType.UNKNOWN
                }

                callLogs.add(
                    CallLogModel(
                        name = userName?:"Unknown",
                        number = phoneNumber,
                        type = callDirection.toString(),
                        duration = callDuration,
                        date  = callDate.toString()
                    )
                )
//                callLogs.add("📞 Number: $phoneNumber\n📅 Date: $callDayTime\n🔹 Type: $callDirection\n⏳ Duration: $callDuration sec \n $name")
            }
        }
    } catch (e: SecurityException) {
        Log.e("CallLogError", "Permission not granted: ${e.message}")
    }
    return callLogs.take(20)
}