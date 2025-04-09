package com.example.child_monitoring_app.ui.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.net.Uri
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.child_monitoring_app.activity.BlockOverlayActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class WebBlockService : AccessibilityService() {

    private val supportedBrowsers = mapOf(
        "com.android.chrome" to "com.android.chrome:id/url_bar",
        "org.mozilla.firefox" to "org.mozilla.firefox:id/url_bar",
        "com.opera.browser" to "com.opera.browser:id/url_field"
    )

    private val blockedSites = listOf("x.com", "amazon.in", "reddit.com")

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.packageName == null || event.source == null) return
        val pkg = event.packageName.toString()
        val urlBarId = supportedBrowsers[pkg] ?: return
        val url = getCurrentUrl(event.source!!, urlBarId) ?: return

        if (blockedSites.any { url.contains(it, ignoreCase = true) }) {
            showBlockScreen(url)
        }
    }

    private fun getCurrentUrl(rootNode: AccessibilityNodeInfo, urlBarId: String): String? {
        return rootNode.findAccessibilityNodeInfosByViewId(urlBarId)
            ?.firstOrNull()?.text?.toString()
    }

    private fun showBlockScreen(url: String) {
        val intent = Intent(this, BlockOverlayActivity::class.java).apply {
            putExtra("blockedUrl", url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
    }

    override fun onInterrupt() {}
}

