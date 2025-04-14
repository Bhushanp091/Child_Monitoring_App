package com.example.child_monitoring_app.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.child_monitoring_app.core.preference.SharedPreference.APP_LAUNCH_PREFS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AppLaunchTrackerService : AccessibilityService() {

    private lateinit var prefs: SharedPreferences
    private val excludedApps = listOf(
        "com.android.launcher", "com.google.android.inputmethod.latin",
        "com.google.android.packageinstaller", "com.google.android.permissioncontroller",
        "com.android.permissioncontroller", "com.android.systemui"
    )

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences(APP_LAUNCH_PREFS, Context.MODE_PRIVATE)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            if (!excludedApps.contains(packageName)) {
                val count = prefs.getInt(packageName, 0) + 1
                prefs.edit().putInt(packageName, count).apply()
                Log.d("AppLaunchTracker", "$packageName launched $count times")
            }
        }
    }

    override fun onInterrupt() {}
}