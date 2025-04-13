package com.example.child_monitoring_app.features.home.screen

import android.app.usage.UsageStatsManager
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.child_monitoring_app.features.auth.AuthViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.core.common.CommonButton
import com.example.child_monitoring_app.core.navigation.Screen
import com.example.child_monitoring_app.core.style_guide.GrayGradient
import com.example.child_monitoring_app.core.style_guide.GreenGradient
import com.example.child_monitoring_app.core.style_guide.OrangeGradient
import com.example.child_monitoring_app.core.style_guide.PinkGradient
import com.example.child_monitoring_app.core.style_guide.Text.RegularText
import com.example.child_monitoring_app.core.style_guide.Text.SmallText
import com.example.child_monitoring_app.core.style_guide.Text.SubHeadingText
import com.example.child_monitoring_app.core.style_guide.VioletGradient
import com.example.child_monitoring_app.core.style_guide.YellowGradient
import com.example.child_monitoring_app.core.util.CommonUtil
import com.example.child_monitoring_app.core.util.CommonUtil.formatMillisToTime
import com.example.child_monitoring_app.features.app_usage.AppUsageInfo
import com.example.child_monitoring_app.features.app_usage.AppUsageViewModel
import com.example.child_monitoring_app.features.app_usage.CallLogModel
import com.example.child_monitoring_app.features.app_usage.hasUsagePermission
import com.example.child_monitoring_app.features.call_log_history.convertTimestampToTimeOnly
import com.example.child_monitoring_app.features.home.component.HomeScreenTopBar
import com.example.child_monitoring_app.features.network.NetworkStatusTracker
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.launch
import network.chaintech.sdpcomposemultiplatform.sdp
import java.util.Calendar


@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    appUsageViewModel: AppUsageViewModel,
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
//    val networkStatusTracker = NetworkStatusTracker(context)
//    val isConnected by networkStatusTracker.networkStatus.collectAsState()
//    var batteryLevel by remember { mutableStateOf(getBatteryPercentage(context)) }
    var isConnected by remember { mutableStateOf(false) }
    var batteryLevel by remember { mutableStateOf("") }
    val parenId = SharedPreference.getParentId(context) ?: ""
    var selectedInterval = remember { mutableStateOf(UsageStatsManager.INTERVAL_DAILY) }
    val flag = remember { mutableStateOf(true) }
    val callLogFlag = remember { mutableStateOf(true) }

    LaunchedEffect(selectedInterval.value) {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(selectedInterval.value, -1) // -1 Day, -1 Week, or -1 Month
        val startTime = calendar.timeInMillis
        if (hasUsagePermission(context)) {
//            usageData = getAppUsageStats(context, startTime, endTime)
            appUsageViewModel.firebaseManager.fetchAppUsageFromFirebase(
                parenId,
                appUsageViewModel.childId.value
            ) {
                if (flag.value) {
                    appUsageViewModel.usageData.value = it
                    flag.value = !flag.value
                }
            }
        }
    }
    LaunchedEffect(Unit) {

        launch {
            appUsageViewModel.firebaseManager.fetchCallLogsFromFirebase(
                parenId,
                appUsageViewModel.childId.value
            ) {
                if (callLogFlag.value) {
                    appUsageViewModel.callLogsMain.value = it
                    callLogFlag.value = !callLogFlag.value
                }
            }
        }

        launch {
            appUsageViewModel.firebaseManager.fetchBatteryAndNetworkData(
                parenId,
                appUsageViewModel.childId.value,
            ){battery,isActive->
                println("Battery Percentage $battery $isActive")
                isConnected = isActive
                batteryLevel = battery.toString()
            }
        }
    }

    val features = listOf(
        MonitoringFeature(
            "App Usage", R.drawable.baseline_apps_24, Screen.AppUsage.route,
            YellowGradient
        ),
        MonitoringFeature(
            "Call History", R.drawable.ic_contact, Screen.CallHistory.route,
            GreenGradient
        ),
        MonitoringFeature(
            "Location",
            R.drawable.baseline_location_on_24,
            Screen.LocationScreen.route,
            VioletGradient
        ),
        MonitoringFeature(
            "App Blocker", R.drawable.baseline_app_blocking_24, Screen.AppBlocker.route,
            OrangeGradient
        ),
    )

    val appUsageList = appUsageViewModel.usageData.value.take(4)
    val callHistoryList = appUsageViewModel.callLogsMain.value.take(4)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFF0F4FF), Color(0xFFFFFFFF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.sdp))

            HomeScreenTopBar(
                modifier = Modifier,
                avatar = R.drawable.child_image,
                name = authViewModel.childName.value,
                isActive = isConnected,
                batteryPercentage = batteryLevel
            ){onNavigate(Screen.ShowChildList.route)}

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubHeadingText.SemiBold(title = "Features", modifier = Modifier.padding(start = 3.sdp))

                TextButton(
                    onClick = { onNavigate(Screen.Features.route) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF4A6FFF)
                    )
                ) {
                    RegularText.Medium(
                        title = "See All",
                        textColor = Color(0xFF4A6FFF)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .height(350.dp) // Fixed height for grid
                    .nestedScroll(remember { object : NestedScrollConnection {} }),
                content = {
                    items(features) { feature ->
                        FeatureCardWithImage(feature) {
                            onNavigate(feature.route)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppUsageSection(appUsageList, onSeeAllClick = { onNavigate(Screen.AppUsage.route) })

            Spacer(modifier = Modifier.height(24.dp))

            CallHistorySection(
                callHistoryList,
                onSeeAllClick = { onNavigate(Screen.CallHistory.route) })

            Spacer(modifier = Modifier.height(24.dp))

            CommonButton(
                text = "Log Out",
                onClick = {
                    SharedPreference.logout(context)
                    authViewModel.logOut()
                    onNavigate(Screen.PreLogin.route)
                },
                containerColor = Color.Red
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AppUsageSection(
    appUsageList: List<AppUsageInfo>,
    onSeeAllClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubHeadingText.SemiBold(title = "App Usage", modifier = Modifier.padding(start = 3.sdp))

            TextButton(
                onClick = onSeeAllClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF4A6FFF)
                )
            ) {
                RegularText.Medium(
                    title = "See All",
                    textColor = Color(0xFF4A6FFF)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                appUsageList.forEachIndexed { index, appUsage ->
                    AppUsageItem(appUsage)
                    if (index < appUsageList.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFEEF2FF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppUsageItem(appUsage: AppUsageInfo) {

    val context = LocalContext.current
    val appIcon: Drawable? = remember {
        try {
            context.packageManager.getApplicationIcon(appUsage.packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F7FF))
                .padding(8.dp)
        ) {
            Image(
                painter = rememberDrawablePainter(drawable = appIcon),
                contentDescription = appUsage.appName,
                modifier = Modifier.size(32.dp)
            )
            if (appIcon != null) {
                Image(
                    painter = rememberDrawablePainter(drawable = appIcon),
                    contentDescription = appUsage.appName,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = appUsage.appName,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            // App name with SemiBold style
            SubHeadingText.SemiBold(
                title = appUsage.appName,
                textColor = Color(0xFF2A3252)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Launch count
//            SmallText.Medium(
//                title = "${appUsage.launchCount} launches today",
//                textColor = Color(0xFF9AA1B9)
//            )
        }

        // Usage time with Medium style
        Column(
            horizontalAlignment = Alignment.End
        ) {
            RegularText.Medium(
                title = formatMillisToTime(appUsage.usageTime.toLong()),
                textColor = Color(0xFF2A3252)
            )

            SmallText.Medium(
                title = "Today",
                textColor = Color(0xFF9AA1B9)
            )
        }
    }
}

@Composable
fun CallHistorySection(
    callHistoryList: List<CallLogModel>,
    onSeeAllClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubHeadingText.SemiBold(
                title = "Call History",
                modifier = Modifier.padding(start = 3.sdp)
            )

            TextButton(
                onClick = onSeeAllClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF4A6FFF)
                )
            ) {
                RegularText.Medium(
                    title = "See All",
                    textColor = Color(0xFF4A6FFF)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                callHistoryList.forEachIndexed { index, callHistory ->
                    CallHistoryItem(callHistory)
                    if (index < callHistoryList.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFEEF2FF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CallHistoryItem(callHistory: CallLogModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Call type icon with appropriate background colors

        val icon = when (callHistory.type) {
            "MISSED" -> R.drawable.call_missed
            "RECEIVED" -> R.drawable.call_received
            "MADE" -> R.drawable.call_made
            else -> {
                R.drawable.call_missed
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when (icon) {
                        R.drawable.call_received -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                        R.drawable.call_made -> Color(0xFF2196F3).copy(alpha = 0.1f)
                        else -> Color(0xFFFF5252).copy(alpha = 0.1f)
                    }
                )
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Call Type",
                tint = when (icon) {
                    R.drawable.call_received -> Color(0xFF4CAF50)
                    R.drawable.call_made -> Color(0xFF2196F3)
                    else -> Color(0xFFFF5252)
                }
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            // Contact name with SemiBold style
            SubHeadingText.SemiBold(
                title = callHistory.name,
                textColor = Color(0xFF2A3252)
            )

            SmallText.Medium(
                title = CommonUtil.convertTimestampToTimeOnly(callHistory.duration.toLong()),
                textColor = Color(0xFF9AA1B9)
            )
        }

        RegularText.Medium(
            title = callHistory.duration + " sec",
        )
    }
}


@Composable
fun FeatureCardWithImage(
    feature: MonitoringFeature,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(160.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.linearGradient(feature.brush))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = feature.iconResId),
                    contentDescription = feature.title,
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.height(12.dp))
                SubHeadingText.Medium(
                    title = feature.title,
                    textAlign = TextAlign.Center,
                    textColor = Color.White
                )
            }
        }
    }
}


// Data classes for our new sections
data class AppUsageData(
    val appName: String,
    val iconResId: Int,
    val usageTime: String,
    val launchCount: Int,
    val usagePercentage: Float = 0f // Keeping this for compatibility
)

data class CallHistoryData(
    val contactName: String,
    val callTime: String,
    val callTypeIcon: Int,
    val callDuration: String
)


data class MonitoringFeature(
    val title: String,
    val iconResId: Int,
    val route: String,
    val brush: List<Color>
)
