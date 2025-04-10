package com.example.child_monitoring_app.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class AppLaunchTrackerService : AccessibilityService() {

    private val targetPackages = listOf(
        "com.instagram.android",
        "com.android.chrome"
    )

    private lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences("app_launch_prefs", Context.MODE_PRIVATE)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            if (targetPackages.contains(packageName)) {
                val count = prefs.getInt(packageName, 0) + 1
                prefs.edit().putInt(packageName, count).apply()
                Log.d("AppLaunchTracker", "$packageName launched $count times")
                // Optional: upload to Firebase here
            }
        }
    }

    override fun onInterrupt() {}
}
