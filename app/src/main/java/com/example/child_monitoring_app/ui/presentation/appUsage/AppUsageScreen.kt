package com.example.child_monitoring_app.ui.presentation.appUsage

import android.app.AppOpsManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import java.util.Calendar
import java.util.concurrent.TimeUnit
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.ui.CommonUtil.formatMillisToTime
import com.example.child_monitoring_app.ui.data.SharedPreference
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import network.chaintech.sdpcomposemultiplatform.sdp

@Composable
fun AppUsageScreen(appUsageViewModel: AppUsageViewModel,modifier: Modifier) {

    var selectedInterval by remember { mutableStateOf(UsageStatsManager.INTERVAL_DAILY) }
    val context = LocalContext.current
    val parenId = SharedPreference.getParentId(context) ?: ""
    val childId = remember { mutableStateOf("Child") }
    val flag = remember { mutableStateOf(true) }


    LaunchedEffect(selectedInterval) {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(selectedInterval, -1) // -1 Day, -1 Week, or -1 Month
        val startTime = calendar.timeInMillis
        if (hasUsagePermission(context)) {
//            usageData = getAppUsageStats(context, startTime, endTime)
            appUsageViewModel.firestoreManager.fetchAppUsageFromFirebase(parenId,childId.value){
                if (flag.value) {
                    println("Fetch call Logs $it")
                    appUsageViewModel.usageData.value = it
                    flag.value = !flag.value
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (!hasUsagePermission(context)) {
            Button(onClick = {
                requestUsagePermission(context)
            }) {
                Text("Grant Usage Access")
            }
        } else {
//            if (appUsageViewModel.showLoader.value){
//                CircularProgressIndicator()
//            }else{
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Spacer(modifier = Modifier.height(10.sdp))
                        Row {
                            Button(onClick = { selectedInterval = Calendar.DAY_OF_MONTH }) {
                                Text("Daily")
                            }
                            Spacer(modifier = Modifier.width(8.sdp))
                            Button(onClick = { selectedInterval = Calendar.WEEK_OF_YEAR }) {
                                Text("Weekly")
                            }
                            Spacer(modifier = Modifier.width(8.sdp))
                            Button(onClick = { selectedInterval = Calendar.MONTH }) {
                                Text("Monthly")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.sdp))
                    }
                    items(appUsageViewModel.usageData.value.toList()) { appUsageInfo ->
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
            Text(text = appUsageInfo.appName, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Usage Time: $formattedTime",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
    HorizontalDivider(
        thickness = 1.sdp,
        color = Color.LightGray,
        modifier = Modifier.fillMaxWidth()
    )
}

fun hasUsagePermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

fun requestUsagePermission(context: Context) {
    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    context.startActivity(intent)
}


