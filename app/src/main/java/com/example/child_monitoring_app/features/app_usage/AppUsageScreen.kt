package com.example.child_monitoring_app.features.app_usage

import android.app.AppOpsManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.provider.Settings
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
    val formattedTime = formatMillisToTime(appUsageInfo.usageTime.toLong())

    val appIcon: Drawable? = remember {
        try {
            context.packageManager.getApplicationIcon(appUsageInfo.packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }


    Row(
        modifier = Modifier
            .background(color = Color.White)
            .padding(vertical = 10.sdp)
            .padding(start = 8.sdp, end = 8.sdp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (appIcon != null) {
            Image(
                painter = rememberDrawablePainter(drawable = appIcon),
                contentDescription = "${appUsageInfo.appName} Icon",
                modifier = Modifier
                    .size(38.sdp)
                    .clip(CircleShape)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "${appUsageInfo.appName} Icon",
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .size(38.sdp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.sdp)) // Space between icon and text

        // Column for App Name and Usage Time
        Column {
            SubHeadingText.SemiBold(
                title = appUsageInfo.appName
            )
            RegularText.Medium(
                title = "Usage Time: $formattedTime",
            )
        }
    }
    HorizontalDivider(
        thickness = 1.sdp,
        color = Color.LightGray,
        modifier = Modifier.fillMaxWidth()
    )
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



