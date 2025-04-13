package com.example.child_monitoring_app.app

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import com.example.child_monitoring_app.R
import kotlin.system.exitProcess
// BlockOverlayActivity.kt
class BlockOverlayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set window properties for overlay
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // This line won't work here - TYPE_APPLICATION_OVERLAY needs to be set when adding the view to WindowManager
        // window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)

        // Ensure this activity stays on top and can't be bypassed
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)

        setContent {
            BlockedUI {
                // Instead of closing the app entirely, just finish this activity
                // This will prevent the brief flash of the blocked app
                finish()
                // We'll manage killing the app through the service
            }
        }
    }

    // Override back button to prevent bypassing

}

@Composable
fun BlockedUI(onCloseApp: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.baseline_block_24),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Red),
                modifier = Modifier.size(80.dp)
            )

            Text(
                text = "App Blocked",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "This app is restricted.\nTime to focus on something else!",
                textAlign = TextAlign.Center,
                color = Color.LightGray,
                fontSize = 16.sp
            )

            Button(
                onClick = onCloseApp,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(text = "Close App", color = Color.White)
            }
        }
    }
}

// AppBlockingService.kt
class AppBlockingService : Service() {
    private val blockedPackages = mutableSetOf<String>()
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var isOverlayShown = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        startForeground()
        startMonitoring()
    }

    private fun startForeground() {
        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "app_blocking_channel",
                "App Blocking",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Create notification
        val notification = NotificationCompat.Builder(this, "app_blocking_channel")
            .setContentTitle("App Monitoring Active")
            .setContentText("Monitoring for restricted apps")
            .setSmallIcon(R.drawable.baseline_block_24)
            .build()

//        startForeground(1, notification)
    }

    private fun startMonitoring() {
        // Load blocked packages from preferences
        updateBlockedPackages()

        // Start a thread to continuously check the foreground app
        Thread {
            while (true) {
                val currentApp = getCurrentForegroundApp()
                if (blockedPackages.contains(currentApp) && !isOverlayShown) {
                    showBlockOverlay(currentApp)
                } else if (!blockedPackages.contains(currentApp) && isOverlayShown) {
                    hideBlockOverlay()
                }

                try {
                    Thread.sleep(500) // Check every 500ms
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun updateBlockedPackages() {
        // This would load your blocked packages from SharedPreferences or database
        blockedPackages.clear()
        blockedPackages.add("com.example.blockedapp1")
        blockedPackages.add("com.example.blockedapp2")
        // Add more blocked packages as needed
    }

    private fun getCurrentForegroundApp(): String {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()

        // Get usage stats for the last 10 seconds
        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, time - 10000, time
        )

        // Find the app that was used last
        if (usageStatsList != null && usageStatsList.isNotEmpty()) {
            var recentApp = ""
            var recentTime = 0L

            for (usageStats in usageStatsList) {
                if (usageStats.lastTimeUsed > recentTime) {
                    recentTime = usageStats.lastTimeUsed
                    recentApp = usageStats.packageName
                }
            }

            return recentApp
        }

        return ""
    }

    private fun showBlockOverlay(packageName: String) {
        if (Settings.canDrawOverlays(this)) {
            // Using a proper overlay instead of starting an activity
            Handler(Looper.getMainLooper()).post {
                // Create overlay params
                val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    else
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT
                )

                // Create ComposeView for our overlay
                val composeView = ComposeView(this)
                composeView.setContent {
                    BlockedUI {
                        // Close both the overlay and the blocked app
                        hideBlockOverlay()

                        // Kill the blocked app process
                        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//                        am.killBackgroundProcesses(packageName)

                        // Force home screen
                        val homeIntent = Intent(Intent.ACTION_MAIN)
                        homeIntent.addCategory(Intent.CATEGORY_HOME)
                        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(homeIntent)
                    }
                }

                // Add the view to window manager
                windowManager?.addView(composeView, params)
                overlayView = composeView
                isOverlayShown = true
            }
        } else {
            // Launch the overlay activity as fallback if permission isn't granted
            val intent = Intent(this, BlockOverlayActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            isOverlayShown = true
        }
    }

    private fun hideBlockOverlay() {
        Handler(Looper.getMainLooper()).post {
            if (overlayView != null) {
                windowManager?.removeView(overlayView)
                overlayView = null
            }
            isOverlayShown = false
        }
    }

    override fun onDestroy() {
        hideBlockOverlay()
        super.onDestroy()
    }
}