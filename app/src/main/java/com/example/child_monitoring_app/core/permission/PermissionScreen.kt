package com.example.child_monitoring_app.core.permission

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import com.example.child_monitoring_app.core.navigation.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(modifier: Modifier = Modifier, onNavigate: (String) -> Unit) {

    val context = LocalContext.current
    val accessibilityGranted = remember { mutableStateOf(isAccessibilityEnabled(context)) }
    val usageStatsGranted = remember { mutableStateOf(isUsageStatsPermissionGranted(context)) }
    val drawOverAppsGranted = remember { mutableStateOf(Settings.canDrawOverlays(context)) }
//    val notificationGranted = remember { mutableStateOf(NotificationManagerCompat.from(context).areNotificationsEnabled()) }

    val callPermission = Manifest.permission.READ_CALL_LOG
    val contactsPermission = Manifest.permission.READ_CONTACTS
    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(callPermission, contactsPermission, locationPermission)
    )

    val areAllRuntimePermissionsGranted = permissionState.allPermissionsGranted
    val areAllSpecialPermissionsGranted = accessibilityGranted.value &&
            usageStatsGranted.value &&
            drawOverAppsGranted.value

    LaunchedEffect(
        areAllRuntimePermissionsGranted,
        areAllSpecialPermissionsGranted
    ) {
        if (areAllRuntimePermissionsGranted && areAllSpecialPermissionsGranted) {

        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Permissions", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        PermissionItem("Accessibility", accessibilityGranted.value) {
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        PermissionItem("Usage Access", usageStatsGranted.value) {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        PermissionItem("Draw Over Apps", drawOverAppsGranted.value) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            context.startActivity(intent)
        }

//        PermissionItem("Notifications", notificationGranted.value) {
//            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
//                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
//            }
//            context.startActivity(intent)
//        }

        permissionState.permissions.forEach { perm ->
            val granted = perm.status.isGranted
            PermissionItem(perm.permission.split(".").last(), granted) {
                permissionState.launchMultiplePermissionRequest()
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Refresh states
                accessibilityGranted.value = isAccessibilityEnabled(context)
                usageStatsGranted.value = isUsageStatsPermissionGranted(context)
                drawOverAppsGranted.value = Settings.canDrawOverlays(context)
//                notificationGranted.value = NotificationManagerCompat.from(context).areNotificationsEnabled()
            }
        ) {
            Text("Refresh Permissions")
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onNavigate(Screen.ChildDashBoard.route)
            }
        ) {
            Text("Refresh Permissions")
        }
    }
}

@Composable
fun PermissionItem(name: String, granted: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, modifier = Modifier.weight(1f), fontSize = 16.sp)
        Text(
            text = if (granted) "Granted" else "Not Granted",
            color = if (granted) Color(0xFF4CAF50) else Color(0xFFF44336),
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun isAccessibilityEnabled(context: Context): Boolean {
    val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    return am.isEnabled
}

fun isUsageStatsPermissionGranted(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}
