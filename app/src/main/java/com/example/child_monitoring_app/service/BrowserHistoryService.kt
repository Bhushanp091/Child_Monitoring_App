package com.example.child_monitoring_app.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class BrowserHistoryService : AccessibilityService() {

    private val supportedBrowsers = listOf(
        "com.android.chrome",
        "org.mozilla.firefox",
        "com.opera.browser"
    )

    private var lastUrl: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.source == null) return

        val packageName = event.packageName?.toString() ?: return
        if (!supportedBrowsers.contains(packageName)) return

        // Important: Only look for WINDOW_STATE_CHANGED or WINDOW_CONTENT_CHANGED events
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                val rootNode = rootInActiveWindow ?: return
                val url = findUrlText(rootNode)

                if (url != null && url != lastUrl && isValidUrl(url)) {
                    lastUrl = url
                    Log.d("BrowserTracker", "🌐 Visited URL: $url")

                    // 🔥 TODO: Save to Firebase if needed
                }
            }
        }
    }

    private fun findUrlText(node: AccessibilityNodeInfo?): String? {
        if (node == null) return null

        // Avoid focused EditText to skip capturing keystrokes
        if (node.className == "android.widget.EditText" && node.text != null && !node.isFocused) {
            val urlText = node.text.toString()
            if (isValidUrl(urlText)) return urlText
        }

        for (i in 0 until node.childCount) {
            val childText = findUrlText(node.getChild(i))
            if (childText != null) return childText
        }

        return null
    }

    private fun isValidUrl(text: String): Boolean {
        return text.startsWith("http://") || text.startsWith("https://")
    }

    override fun onInterrupt() {}
}

