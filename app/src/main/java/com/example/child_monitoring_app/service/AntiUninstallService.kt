package com.example.child_monitoring_app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class AppInfoBlockService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            val className = event.className?.toString() ?: return

            // Detect if the user has opened your app's info screen
            if ((packageName == "com.android.settings" || packageName.contains("settings")) &&
                className.contains("Settings") || className.contains("AppInfoActivity")
            ) {
                // Check if it's your app's info screen
                if (event.text.any { it.toString().contains("com.example.child_monitoring_app") }) {
                    // Immediately redirect to home
                    val homeIntent = Intent(Intent.ACTION_MAIN)
                    homeIntent.addCategory(Intent.CATEGORY_HOME)
                    homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(homeIntent)
                }
            }
        }
    }

    override fun onInterrupt() {}
}
