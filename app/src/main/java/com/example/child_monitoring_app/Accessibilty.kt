package com.example.child_monitoring_app

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class UrlLoggingService : AccessibilityService() {

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
            packageNames = arrayOf("com.android.chrome") // You can add more like Firefox, Edge
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
        }
        serviceInfo = info
        Log.d("UrlLoggingService", "Service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        val rootNode = rootInActiveWindow ?: return
        val url = extractUrl(rootNode)

        url?.let {
            Log.d("UrlLoggingService", "Detected URL: $it")
//            uploadUrlToFirestore(it)
        }
    }

    private fun extractUrl(node: AccessibilityNodeInfo?): String? {
        if (node == null) return null

        if (node.className == "android.widget.EditText" && node.text != null) {
            val text = node.text.toString()
            if (text.startsWith("http") || text.contains(".")) {
                return text
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val url = extractUrl(child)
            if (url != null) return url
        }

        return null
    }

    override fun onInterrupt() {}
}
