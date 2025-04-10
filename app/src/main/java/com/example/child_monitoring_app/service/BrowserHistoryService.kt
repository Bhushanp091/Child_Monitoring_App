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

        val rootNode = rootInActiveWindow ?: return

        val urlNode = findUrlNode(rootNode)
        urlNode?.let {
            val url = it.text?.toString()
            if (!url.isNullOrBlank() && url != lastUrl) {
                lastUrl = url
                Log.d("BrowserTracker", "Visited URL: $url")

                // Optional: Save to Firebase here
            }
        }
    }

    private fun findUrlNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null

        if (node.className == "android.widget.EditText" && node.isFocused && node.text != null) {
            return node
        }

        for (i in 0 until node.childCount) {
            val child = findUrlNode(node.getChild(i))
            if (child != null) return child
        }

        return null
    }

    override fun onInterrupt() {}
}

