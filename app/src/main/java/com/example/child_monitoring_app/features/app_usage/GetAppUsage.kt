package com.example.child_monitoring_app.features.app_usage

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.LruCache

fun getAppUsageStats(context: Context, startTime: Long, endTime: Long): List<AppUsageInfo> {
    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    val usageStatsList = usageStatsManager.queryUsageStats(
        UsageStatsManager.INTERVAL_DAILY,
        startTime,
        endTime
    )

    val packageManager = context.packageManager

    return usageStatsList
        .filter { it.totalTimeInForeground > 0 }
        .mapNotNull { usageStats ->
            try {
                val appInfo = packageManager.getApplicationInfo(usageStats.packageName, 0)
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                val icon = appInfo.loadIcon(packageManager)

                AppUsageInfo(
                    packageName = usageStats.packageName,
                    appName = appName,
                    usageTime = usageStats.totalTimeInForeground.toString(),
                    lastTimeUsed = usageStats.lastTimeUsed.toString()
                )
            } catch (e: Exception) {
                null
            }
        }
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
