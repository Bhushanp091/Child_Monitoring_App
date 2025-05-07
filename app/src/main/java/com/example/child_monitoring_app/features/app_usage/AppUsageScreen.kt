package com.example.child_monitoring_app.features.app_usage

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.util.Calendar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.core.style_guide.Text.RegularText
import com.example.child_monitoring_app.core.style_guide.Text.SubHeadingText
import com.example.child_monitoring_app.features.home.screen.AppUsageItem
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.delay
import network.chaintech.sdpcomposemultiplatform.sdp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// Updated UI Component
@Composable
fun AppUsageScreen(
    appUsageViewModel: AppUsageViewModel,
    modifier: Modifier = Modifier
) = with(appUsageViewModel) {

    val context = LocalContext.current
    val parentId = SharedPreference.getParentId(context) ?: ""
    val isLoading = remember { mutableStateOf(false) }

    LaunchedEffect(selectedInterval.value) {
        isLoading.value = true

        // Simple delay to ensure loading indicator shows
        delay(200)
        println("appUsage Fetch from firebase")

        firebaseManager.fetchAppUsageFromFirebase(
            parentId = parentId,
            childId = childId.value,
            intervalType = selectedInterval.value.toString()
        ) { usageList ->
            isLoading.value = false
            usageData.value = usageList
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Improved UI with active interval indicator
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Current interval title
                Text(
                    text = "${selectedInterval.value.name.lowercase().capitalize()} App Usage",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Time range divider
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Interval selector buttons
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    IntervalButton(
                        label = "Daily",
                        interval = AppUsageViewModel.IntervalType.DAILY,
                        selectedInterval = selectedInterval
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IntervalButton(
                        label = "Weekly",
                        interval = AppUsageViewModel.IntervalType.WEEKLY,
                        selectedInterval = selectedInterval
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IntervalButton(
                        label = "Monthly",
                        interval = AppUsageViewModel.IntervalType.MONTHLY,
                        selectedInterval = selectedInterval
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Time range display
                val timeRangeText = when (selectedInterval.value) {
                    AppUsageViewModel.IntervalType.DAILY -> {
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        "Today (${dateFormat.format(Date())})"
                    }

                    AppUsageViewModel.IntervalType.WEEKLY -> {
                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

                        // Get start of week
                        val tempCalendar = Calendar.getInstance()
                        tempCalendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                        val startDate = dateFormat.format(tempCalendar.time)

                        // Get end of week
                        tempCalendar.add(Calendar.DAY_OF_WEEK, 6)
                        val endDate = dateFormat.format(tempCalendar.time)

                        "This Week ($startDate - $endDate)"
                    }

                    AppUsageViewModel.IntervalType.MONTHLY -> {
                        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                        "This Month (${monthFormat.format(Date())})"
                    }
                }

                Text(
                    text = timeRangeText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isLoading.value) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (usageData.value.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No app usage data available for this time period",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                // Display total usage time
                item {
                    val totalMillis = usageData.value.sumOf {
                        it.usageTimeMillis.toLongOrNull() ?: 0L
                    }

                    val hours = TimeUnit.MILLISECONDS.toHours(totalMillis)
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(totalMillis) % 60

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccessTime,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Total Usage Time",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "$hours hr $minutes min",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // App usage items
                items(usageData.value) { appUsageInfo ->
                    AppUsageItem2(appUsageInfo)
                }
            }
        }
    }
}

@Composable
fun IntervalButton(
    label: String,
    interval: AppUsageViewModel.IntervalType,
    selectedInterval: MutableState<AppUsageViewModel.IntervalType>
) {
    val isSelected = selectedInterval.value == interval

    Button(
        onClick = { selectedInterval.value = interval },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(8.dp),
//        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun AppUsageItem2(appUsageInfo: AppUsageInfo) {
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

