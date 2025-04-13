package com.example.child_monitoring_app.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.child_monitoring_app.core.permission.isAccessibilityEnabled
import com.example.child_monitoring_app.core.permission.isUsageStatsPermissionGranted

fun areAllPermissionsGranted(context: Context): Boolean {
    // Check special permissions
    val specialPermissionsGranted = isAccessibilityEnabled(context) &&
            isUsageStatsPermissionGranted(context) &&
            Settings.canDrawOverlays(context) &&
            NotificationManagerCompat.from(context).areNotificationsEnabled()

    // Check runtime permissions
    val permissionChecker = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
    val contactsChecker = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
    val locationChecker = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)

    val runtimePermissionsGranted = permissionChecker == PackageManager.PERMISSION_GRANTED &&
            contactsChecker == PackageManager.PERMISSION_GRANTED &&
            locationChecker == PackageManager.PERMISSION_GRANTED

    return specialPermissionsGranted && runtimePermissionsGranted
}