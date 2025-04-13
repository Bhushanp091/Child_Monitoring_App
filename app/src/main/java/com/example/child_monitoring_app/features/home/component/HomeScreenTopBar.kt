package com.example.child_monitoring_app.features.home.component

import com.example.child_monitoring_app.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.core.style_guide.Text.SmallText
import com.example.child_monitoring_app.core.style_guide.Text.SubHeadingText
import network.chaintech.sdpcomposemultiplatform.sdp


@Composable
fun HomeScreenTopBar(
    modifier: Modifier = Modifier,
    avatar: Int = R.drawable.child_image,
    name: String,
    isActive: Boolean,
    batteryPercentage: String,
    onClick:()->Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.sdp)
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Image(
                painter = painterResource(avatar),
                contentDescription = "Child Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFECECEC))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Name & Status
            Column(modifier = Modifier.weight(1f)) {

                SubHeadingText.SemiBold(
                    title = name,
                    textColor = Color.Black
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.Green, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    SmallText.Medium(
                        title = if (isActive) "Active" else "Offline",
                        textColor = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.battery_full_battery_svgrepo_com),
                        contentDescription = "Battery",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    SmallText.Medium(
                        title = "$batteryPercentage%",
                        textColor = Color.Black
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand",
                tint = Color.Gray
            )
        }
    }
}
