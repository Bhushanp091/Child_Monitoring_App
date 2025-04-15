package com.example.child_monitoring_app.features.browser_history

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.child_monitoring_app.core.common.CommonButton

@Composable
fun BrowserHistoryScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        CommonButton(
            text = "Under Maintenance, Thank you",
            onClick = {
//                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                })
            }
        )
    }
}

