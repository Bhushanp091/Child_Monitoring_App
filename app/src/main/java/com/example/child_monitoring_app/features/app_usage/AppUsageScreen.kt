package com.example.child_monitoring_app.features.app_usage

import android.app.AppOpsManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.util.Base64
import androidx.compose.foundation.Image
import java.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.core.util.CommonUtil.formatMillisToTime
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.core.style_guide.Text.RegularText
import com.example.child_monitoring_app.core.style_guide.Text.SubHeadingText
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import network.chaintech.sdpcomposemultiplatform.sdp

@Composable
fun AppUsageScreen(
    appUsageViewModel: AppUsageViewModel,
    modifier: Modifier = Modifier
) = with(appUsageViewModel) {

    val context = LocalContext.current
    val parentId = SharedPreference.getParentId(context) ?: ""

    LaunchedEffect(selectedInterval.value) {
        val interval = when (selectedInterval.value) {
            Calendar.DAY_OF_MONTH -> "daily"
            Calendar.WEEK_OF_YEAR -> "weekly"
            else -> "monthly"
        }

        firebaseManager.fetchAppUsageFromFirebase(
            parentId,
            childId.value,
            intervalType = interval
        ) { usageList ->
            usageData.value = usageList
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Spacer(modifier = Modifier.height(16.sdp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IntervalButton("Daily", Calendar.DAY_OF_MONTH, selectedInterval)
                    Spacer(modifier = Modifier.width(8.sdp))
                    IntervalButton("Weekly", Calendar.WEEK_OF_YEAR, selectedInterval)
                    Spacer(modifier = Modifier.width(8.sdp))
                    IntervalButton("Monthly", Calendar.MONTH, selectedInterval)
                }
                Spacer(modifier = Modifier.height(16.sdp))
            }

            if (usageData.value.isEmpty()) {
                item {
                    Text(
                        "No usage data available",
                        modifier = Modifier.padding(16.sdp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(usageData.value) { appUsageInfo ->
                    AppUsageItem(appUsageInfo)
                }
            }
        }
    }
}

@Composable
fun AppUsageItem(appUsageInfo: AppUsageInfo) {
    val context = LocalContext.current

    // No need to convert, we already have the formatted time
    val formattedTime = appUsageInfo.usageTime

    val appIcon: Drawable? = remember {
        try {
            context.packageManager.getApplicationIcon(appUsageInfo.packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.sdp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.sdp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.sdp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            if (appIcon != null) {
                Image(
                    painter = rememberDrawablePainter(drawable = appIcon),
                    contentDescription = "${appUsageInfo.appName} Icon",
                    modifier = Modifier
                        .size(40.sdp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.sdp)
                        .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = appUsageInfo.appName.firstOrNull()?.toString() ?: "?",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.sdp))

            // App Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                SubHeadingText.SemiBold(
                    title = appUsageInfo.appName
                )

                Spacer(modifier = Modifier.height(4.sdp))

                RegularText.Medium(
                    title = "Usage Time: $formattedTime"
                )

                // Show last used time if available
                if (appUsageInfo.lastTimeUsed.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.sdp))
                    Text(
                        text = "Last used: ${appUsageInfo.lastTimeUsed}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
// Helper function to remember drawable as a painter
@Composable
fun rememberDrawablePainter(drawable: Drawable): Painter {
    return remember(drawable) {
        DrawablePainter(drawable)
    }
}

// Custom painter for drawables
class DrawablePainter(private val drawable: Drawable) : Painter() {
    override val intrinsicSize: androidx.compose.ui.geometry.Size
        get() = Size(drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())

    override fun DrawScope.onDraw() {
        drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())

        // Save the current canvas state
        drawContext.canvas.save()

        drawable.draw(drawContext.canvas.nativeCanvas)

        // Restore the canvas state
        drawContext.canvas.restore()
    }
}

@Composable
fun IntervalButton(
    label: String,
    calendarConstant: Int,
    selectedInterval: MutableState<Int>
) {
    val isSelected = selectedInterval.value == calendarConstant
    Button(
        onClick = { selectedInterval.value = calendarConstant },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(label)
    }
}



