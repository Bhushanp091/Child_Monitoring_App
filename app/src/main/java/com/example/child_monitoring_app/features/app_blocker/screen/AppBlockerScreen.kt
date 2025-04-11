package com.example.child_monitoring_app.features.app_blocker.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.R

@Composable
fun AppBlockerScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "App Blocker Screen")
        AppUsageRow(modifier,0,"","",false, onToggle = {}){}
    }
}

@Composable
fun AppUsageRow(
    modifier: Modifier = Modifier,
    appIcon: Int,
    appName: String,
    timeUsed: String,
    isBlocked: Boolean,
    onToggle: (Boolean) -> Unit,
    onSetTimeLimit: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFCEEE9), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Icon
        Image(
            painter = painterResource(R.drawable.child_image),
            contentDescription = appName,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // App Name and Action
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = appName,
//                style = AppTypography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = Color.Black
            )
            Text(
                text = "Set Time Limit",
//                style = AppTypography.labelMedium,
                color = Color(0xFF007AFF),
                modifier = Modifier.clickable { onSetTimeLimit() }
            )
        }

        // Time & Toggle
        Text(
            text = timeUsed,
//            style = AppTypography.labelMedium,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = isBlocked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF4CAF50),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}
