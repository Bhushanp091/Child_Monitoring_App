package com.example.child_monitoring_app.ui.presentation.browser

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class BrowserAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("BrowserService", "Accessibility Service Connected!")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d("BrowserService", "Event Received: ${event?.eventType}")

        event?.let {
            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                val packageName = event.packageName?.toString() ?: return
                Log.d("BrowserService", "Detected Package: $packageName")

                if (packageName.contains("chrome") || packageName.contains("browser")) {
                    val url = event.text.toString()
                    Log.d("BrowserService", "Visited URL: $url")
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.d("BrowserService", "Service Interrupted")
    }
}
