package com.example.child_monitoring_app.ui.presentation.browser

import android.accessibilityservice.AccessibilityService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrowserAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "BrowserService"
        private val SUPPORTED_BROWSERS = listOf(
            "com.android.chrome",
            "org.mozilla.firefox",
            "com.opera.browser",
            "com.microsoft.edge"
        )
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility Service Connected ✅")
        startForegroundService()
    }

    private fun startForegroundService() {
        val channelId = "browser_monitoring_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                channelId,
                "Browser Monitoring",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Monitoring Active")
            .setContentText("Tracking browser activity...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return
        if (SUPPORTED_BROWSERS.contains(packageName)) {
            when (event.eventType) {
                AccessibilityEvent.TYPE_VIEW_FOCUSED,
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    rootInActiveWindow?.let { rootNode ->
                        logNodeHierarchy(rootNode) // ✅ Debugging: Log UI hierarchy
                        val urlNode = findUrlBar(rootNode)
                        val urlText = urlNode?.text?.toString()

                        if (!urlText.isNullOrEmpty() && urlText.startsWith("http")) {
                            Log.d(TAG, "Detected URL: $urlText")

                            CoroutineScope(Dispatchers.IO).launch {
                                BrowserHistoryRepository.addBrowserHistory(
                                    urlText,
                                    System.currentTimeMillis().toString()
                                )
                            }
                        }
                    }
                }
                else -> Log.d(TAG, "Unhandled Event: ${event.eventType}")
            }
        }
    }

    // Debugging method to log the UI structure
    private fun logNodeHierarchy(node: AccessibilityNodeInfo?, depth: Int = 0) {
        node ?: return
        val prefix = " ".repeat(depth * 2)
        Log.d(TAG, "$prefix Class: ${node.className}, Text: ${node.text}, ViewId: ${node.viewIdResourceName}")

        for (i in 0 until node.childCount) {
            logNodeHierarchy(node.getChild(i), depth + 1)
        }
    }

    private fun findUrlBar(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        node ?: return null

        if (node.className?.contains("EditText") == true ||
            node.className?.contains("TextView") == true ||
            node.viewIdResourceName?.contains("url") == true ||
            node.viewIdResourceName?.contains("omnibox") == true ||
            node.viewIdResourceName?.contains("search_box") == true ||
            node.viewIdResourceName?.contains("address_bar") == true) {
            return node
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = findUrlBar(child)
            if (result != null) return result
        }

        return null
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility Service Interrupted ❌")
    }
}
