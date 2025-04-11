package com.example.child_monitoring_app.core.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.core.style_guide.Text.SubHeadingText
import network.chaintech.sdpcomposemultiplatform.ssp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonToolbar(
    title: String,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            SubHeadingText.SemiBold(
                title = title,
                textColor = Color(0xFF2A3252),
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.icon_arrow_back),
                        contentDescription = "Back"
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}