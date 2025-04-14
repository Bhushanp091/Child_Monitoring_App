package com.example.child_monitoring_app.core.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.child_monitoring_app.core.style_guide.AppTheme.SoftBlue

@Composable
fun CommonLoader(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .clickable { }
            .background(color = Color.Gray.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = SoftBlue)
    }
}
