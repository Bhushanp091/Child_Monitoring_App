package com.example.child_monitoring_app.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object CommonUtil {

    fun convertTimestampToReadableFormat(timestamp: Long): String {
        val date = Date(timestamp)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // Hour:Minute
        val dateFormat = SimpleDateFormat("dd-MM", Locale.getDefault()) // Date-Month
        return "${timeFormat.format(date)} ${dateFormat.format(date)}"
    }

    fun getDateFromTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat("dd-MM", Locale.getDefault()) // Date-Month
        return dateFormat.format(date)
    }

    fun formatMillisToTime(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
    }


}