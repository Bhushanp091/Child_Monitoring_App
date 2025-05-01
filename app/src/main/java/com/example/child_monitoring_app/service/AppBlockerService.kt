package com.example.child_monitoring_app.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import com.example.child_monitoring_app.app.BlockOverlayActivity

class AppBlockerService : AccessibilityService() {

    private fun getBlockedApps(): Set<String> {
        val prefs = getSharedPreferences("blocker_prefs", Context.MODE_PRIVATE)
        return prefs.getStringSet("blocker_prefs", emptySet()) ?: emptySet()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()
            if (getBlockedApps().contains(packageName)) {
                val intent = Intent(this, BlockOverlayActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val intent = Intent(this, ForegroundKeeperService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }


    override fun onInterrupt() {}
}
