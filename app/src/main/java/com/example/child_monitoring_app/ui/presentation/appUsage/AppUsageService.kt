package com.example.child_monitoring_app.ui.presentation.appUsage

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.provider.Settings
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AppUsageService(private val context: Context) {

    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val hasPermission = mutableStateOf(false)

    private val appNameCache = mutableMapOf<String, String>()
    private val appIconCache = mutableMapOf<String, Drawable?>()
    private val packageManager = context.packageManager

    fun checkForPermission(): Boolean {
        try {
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                System.currentTimeMillis() - 1000 * 60,
                System.currentTimeMillis()
            )
            hasPermission.value = stats.isNotEmpty()
            return hasPermission.value
        } catch (e: Exception) {
            hasPermission.value = false
            return false
        }
    }

    fun openUsageAccessSettings() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    suspend fun getAppUsageData(timeFrame: TimeFrame): List<AppUsageData> = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis

        val startTime = when (timeFrame) {
            TimeFrame.DAILY -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.timeInMillis
            }
            TimeFrame.WEEKLY -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                calendar.timeInMillis
            }
            TimeFrame.MONTHLY -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.timeInMillis
            }
        }

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            startTime,
            endTime
        )

        val packageManager = context.packageManager

        val appUsageList = stats
            .filter { it.totalTimeInForeground > 0 }
            .map { usageStat ->
                try {
                    val appName = getAppName(usageStat.packageName)
                    val appIcon = getAppIcon(usageStat.packageName)

                    AppUsageData(
                        packageName = usageStat.packageName,
                        appName = appName,
                        usageTime = usageStat.totalTimeInForeground,
                        appIcon = appIcon
                    )
                } catch (e: Exception) {
                    null
                }
            }
            .filterNotNull()
            .sortedByDescending { it.usageTime }

        appUsageList
    }

    fun formatTime(timeInMillis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(timeInMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }

    private fun getAppName(packageName: String): String {
        if (appNameCache.containsKey(packageName)) {
            return appNameCache[packageName]!!
        }

        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val appName = packageManager.getApplicationLabel(appInfo).toString()
            appNameCache[packageName] = appName
            appName
        } catch (e: Exception) {
            // Create a more user-friendly name from the package
            val readableName = getReadablePackageName(packageName)
            appNameCache[packageName] = readableName
            readableName
        }
    }

    // Get app icon with proper error handling
    private fun getAppIcon(packageName: String): Drawable? {
        if (appIconCache.containsKey(packageName)) {
            return appIconCache[packageName]
        }

        return try {
            val icon = packageManager.getApplicationIcon(packageName)
            appIconCache[packageName] = icon
            icon
        } catch (e: Exception) {
            try {
                // Try alternative method to get icon
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                val icon = appInfo.loadIcon(packageManager)
                appIconCache[packageName] = icon
                icon
            } catch (e: Exception) {
                // Use system default icon as fallback or null
                try {
                    val defaultIcon = packageManager.defaultActivityIcon
                    appIconCache[packageName] = defaultIcon
                    defaultIcon
                } catch (e: Exception) {
                    appIconCache[packageName] = null
                    null
                }
            }
        }
    }
    private fun getReadablePackageName(packageName: String): String {
        // Extract the last part of the package name and format it
        val lastPart = packageName.split(".").last()
        return lastPart.replaceFirstChar { it.uppercase() }
            .replace(Regex("([a-z])([A-Z])"), "$1 $2") // Add spaces between camel case
    }
}

data class AppUsageData(
    val packageName: String,
    val appName: String,
    val usageTime: Long,
    val appIcon: Drawable?
)

enum class TimeFrame {
    DAILY, WEEKLY, MONTHLY
}