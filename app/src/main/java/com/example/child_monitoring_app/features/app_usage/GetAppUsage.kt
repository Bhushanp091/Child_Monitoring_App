package com.example.child_monitoring_app.features.app_usage

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.Log
import android.util.LruCache
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

fun getAppUsageStats(context: Context, startTime: Long, endTime: Long): List<AppUsageInfo> {
    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    // Use the appropriate interval based on the time range
    val intervalType = when {
        (endTime - startTime) <= 24 * 60 * 60 * 1000 -> UsageStatsManager.INTERVAL_DAILY
        (endTime - startTime) <= 7 * 24 * 60 * 60 * 1000 -> UsageStatsManager.INTERVAL_WEEKLY
        else -> UsageStatsManager.INTERVAL_MONTHLY
    }

    Log.d("AppUsage", "Using interval type: $intervalType for range ${Date(startTime)} to ${Date(endTime)}")

    // Get the raw usage stats from the system
    val usageStatsList = usageStatsManager.queryUsageStats(
        intervalType,
        startTime,
        endTime
    )

    Log.d("AppUsage", "Found ${usageStatsList.size} usage records")

    val packageManager = context.packageManager
    val formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)

    // Create a map to aggregate usage by package
    val aggregatedStats = mutableMapOf<String, Long>()

    // Aggregate the total time for each app across all sessions in the period
    usageStatsList.forEach { stats ->
        if (stats.totalTimeInForeground > 0) {
            val currentTotal = aggregatedStats[stats.packageName] ?: 0L
            aggregatedStats[stats.packageName] = currentTotal + stats.totalTimeInForeground
        }
    }

    Log.d("AppUsage", "Aggregated to ${aggregatedStats.size} unique apps")

    // Create a map for last used time
    val lastUsedMap = mutableMapOf<String, Long>()
    usageStatsList.forEach { stats ->
        val currentLastUsed = lastUsedMap[stats.packageName] ?: 0L
        if (stats.lastTimeUsed > currentLastUsed) {
            lastUsedMap[stats.packageName] = stats.lastTimeUsed
        }
    }

    return aggregatedStats.entries
        .filter { it.value > 0 }
        .mapNotNull { (packageName, totalTime) ->
            try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                val appName = packageManager.getApplicationLabel(appInfo).toString()

                // Format usage time into hours, minutes, seconds
                val hours = TimeUnit.MILLISECONDS.toHours(totalTime)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime) % 60
                val formattedUsageTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                // Get and format last time used
                val lastTimeUsed = lastUsedMap[packageName] ?: 0L
                val formattedLastTimeUsed = formatter.format(Date(lastTimeUsed))

                // Keep raw milliseconds for sorting
                val usageTimeMillis = totalTime.toString()

                // Convert app icon to Base64 string - only if needed for Firebase
                val iconBase64 = ""  // Skip icon conversion here for performance

                Log.d("AppUsage", "App: $appName, Time: $formattedUsageTime ($totalTime ms)")

                AppUsageInfo(
                    packageName = packageName,
                    appName = appName,
                    usageTime = formattedUsageTime,
                    usageTimeMillis = usageTimeMillis,  // Keep raw time for sorting
                    lastTimeUsed = formattedLastTimeUsed,
                    icon = iconBase64
                )
            } catch (e: Exception) {
                Log.e("AppUsage", "Error processing app $packageName: ${e.message}")
                null
            }
        }
        .sortedByDescending { it.usageTimeMillis.toLongOrNull() ?: 0 }
}


fun isUserApp(packageManager: PackageManager, packageName: String): Boolean {
    return try {
        val appInfo = packageManager.getApplicationInfo(packageName, 0)
        val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        val isLauncher = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = packageManager.resolveActivity(isLauncher, PackageManager.MATCH_DEFAULT_ONLY)
        val isDefaultLauncher = resolveInfo?.activityInfo?.packageName == packageName
        !isSystemApp && !isDefaultLauncher
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}
