package com.example.child_monitoring_app.ui.presentation.browser

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrowserAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("BrowserService", "Accessibility Service Connected!")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName.contains("chrome") || packageName.contains("firefox") || packageName.contains("browser")) {
            event.source?.let { nodeInfo ->
                val text = nodeInfo.text?.toString()
                if (!text.isNullOrEmpty() && text.startsWith("http")) {
                    Log.d("BrowserService", "Visited URL: $text")

                    // Store the detected URL in Repository
                    CoroutineScope(Dispatchers.IO).launch {
                        BrowserHistoryRepository.addBrowserHistory(text, System.currentTimeMillis().toString())
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.d("BrowserService", "Service Interrupted")
    }
}
