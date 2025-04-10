package com.example.child_monitoring_app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.child_monitoring_app.app.BlockOverlayActivity

class AppBlockerService : AccessibilityService() {

    private val blockedApps = listOf(
        "in.amazon.mShop.android.shopping",
        "com.google.android.chrome,",
        "com.google.android.youtube"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()
            if (blockedApps.contains(packageName)) {
                val intent = Intent(this, BlockOverlayActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    override fun onInterrupt() {}
}
