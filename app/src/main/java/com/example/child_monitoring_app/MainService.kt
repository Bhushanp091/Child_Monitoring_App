package com.example.child_monitoring_app

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class MyService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No action here for now
    }

    override fun onInterrupt() {
        // Required override but not used here
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val action = event.action
        val keyCode = event.keyCode

        if (action == KeyEvent.ACTION_UP) {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    Log.d("Check", "KeyUp")
                    Toast.makeText(this, "KeyUp", Toast.LENGTH_SHORT).show()
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    Log.d("Check", "KeyDown")
                    Toast.makeText(this, "KeyDown", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return super.onKeyEvent(event)
    }
}
