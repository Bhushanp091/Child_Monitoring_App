package com.example.child_monitoring_app.ui.data.appUsage

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.LruCache
import androidx.compose.runtime.mutableStateOf
import com.example.child_monitoring_app.ui.presentation.appUsage.AppUsageInfo

fun getAppUsageStats(context: Context, startTime: Long, endTime: Long): List<AppUsageInfo> {
    val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
    val packageManager = context.packageManager


    val iconCache = LruCache<String, Drawable>(100)

    // Track both usage time and last used timestamp
    data class UsageData(var totalTime: Long = 0L, var lastUsed: Long = 0L)

    val appUsageMap = mutableMapOf<String, UsageData>()

    var lastForegroundTime = 0L
    var lastPackageName: String? = null

    val event = UsageEvents.Event()
    while (usageEvents.hasNextEvent()) {
        usageEvents.getNextEvent(event)

        when (event.eventType) {
            UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                lastForegroundTime = event.timeStamp
                lastPackageName = event.packageName
            }

            UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                if (lastPackageName != null && lastForegroundTime > 0) {
                    val usageTime = event.timeStamp - lastForegroundTime
                    val usageData = appUsageMap.getOrDefault(lastPackageName, UsageData())
                    usageData.totalTime += usageTime
                    usageData.lastUsed = event.timeStamp
                    appUsageMap[lastPackageName!!] = usageData

                    lastForegroundTime = 0L
                    lastPackageName = null
                }
            }
        }
    }
    return appUsageMap.map { (packageName, usageData) ->
        // First try to get icon from cache
        var icon = iconCache.get(packageName)

        if (icon == null) {
            icon = try {
                packageManager.getApplicationIcon(packageName).also {
                    // Store in cache for future use
                    iconCache.put(packageName, it)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }

        val appName = try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            // Load label using the ApplicationInfo object
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName // Fallback to package name
        }

        AppUsageInfo(
            packageName = packageName,
            appName = appName,
            usageTime = usageData.totalTime.toString(),
            icon = icon.toString(),
            lastTimeUsed = usageData.lastUsed.toString()
        )
    }.sortedByDescending { it.lastTimeUsed }
}
