package com.example.child_monitoring_app.ui.presentation.browser

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.child_monitoring_app.ui.presentation.component.CommonButton

@Composable
fun BrowserHistoryScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        CommonButton(
            text = "Enable Accessibility Permission",
            onClick = {
                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        )
    }
}
//fun requestDrawOverAppsPermission(context: Context) {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//        if (!Settings.canDrawOverlays(context)) {
//            val intent = Intent(
//                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                Uri.parse("package:${context.packageName}")
//            )
//            context.startActivity(intent)
//        } else {
//            Toast.makeText(context, "Overlay permission already granted", Toast.LENGTH_SHORT).show()
//        }
//    }
//}
