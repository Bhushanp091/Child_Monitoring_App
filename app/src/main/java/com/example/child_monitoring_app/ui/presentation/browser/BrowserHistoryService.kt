package com.example.child_monitoring_app.ui.presentation.browser

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BrowserHistoryService {
    suspend fun getBrowserHistory(context: Context): List<Pair<String, String>> {
        return withContext(Dispatchers.IO) {
            val historyList = mutableListOf<Pair<String, String>>()
            val uri = Uri.parse("content://browser/bookmarks")
            val projection = arrayOf("title", "url", "date")

            context.contentResolver.query(uri, projection, null, null, "date DESC")?.use { cursor ->
                val titleIndex = cursor.getColumnIndex("title")
                val urlIndex = cursor.getColumnIndex("url")
                val dateIndex = cursor.getColumnIndex("date")

                while (cursor.moveToNext()) {
                    val title = cursor.getString(titleIndex) ?: "No Title"
                    val url = cursor.getString(urlIndex) ?: "No URL"
                    val date = cursor.getString(dateIndex) ?: "Unknown Date"

                    historyList.add(Pair("$title ($date)", url))
                }
            }
            historyList
        }
    }
}
