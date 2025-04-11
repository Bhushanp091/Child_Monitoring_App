package com.example.child_monitoring_app.features.call_log_history


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.core.util.CommonUtil
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.core.style_guide.Text.RegularText
import com.example.child_monitoring_app.core.style_guide.Text.SmallText
import com.example.child_monitoring_app.core.style_guide.Text.SubHeadingText
import com.example.child_monitoring_app.features.app_usage.AppUsageViewModel
import com.example.child_monitoring_app.features.app_usage.CallLogModel
import network.chaintech.sdpcomposemultiplatform.sdp
import network.chaintech.sdpcomposemultiplatform.ssp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun CallHistoryScreen(
    appUsageViewModel: AppUsageViewModel,
    modifier: Modifier = Modifier
) = with(appUsageViewModel) {
    val context = LocalContext.current
    val parenId = SharedPreference.getParentId(context) ?: ""

    LaunchedEffect(Unit) {
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
                .padding(16.dp)
        ) {
//            // Header Section
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 12.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_call),
//                    contentDescription = "Call History",
//                    tint = Color(0xFF4A6FFF),
//                    modifier = Modifier.size(28.dp)
//                )
//
//                Spacer(modifier = Modifier.width(12.dp))
//
//                SubHeadingText.SemiBold(
//                    title = "Call History",
//                    textColor = Color(0xFF2A3252),
//                )
//            }

//            Spacer(modifier = Modifier.height(8.dp))

            // Call logs count summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CallStatItem(
                        count = appUsageViewModel.callLogsMain.value.count { it.type == "RECEIVED" },
                        label = "Received",
                        iconId = R.drawable.call_received,
                        iconTint = Color(0xFF4CAF50)
                    )

                    CallStatItem(
                        count = appUsageViewModel.callLogsMain.value.count { it.type == "MADE" },
                        label = "Made",
                        iconId = R.drawable.call_made,
                        iconTint = Color(0xFF2196F3)
                    )

                    CallStatItem(
                        count = appUsageViewModel.callLogsMain.value.count { it.type == "MISSED" },
                        label = "Missed",
                        iconId = R.drawable.call_missed,
                        iconTint = Color(0xFFFF5252)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Spacer(modifier = Modifier.height(8.dp))

            // Call Logs List
            if (appUsageViewModel.callLogsMain.value.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "No Calls",
                            tint = Color(0xFFBDC1D3),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SubHeadingText.Medium(
                            title = "No call records found",
                            textColor = Color(0xFF9AA1B9)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Group calls by date
                    val groupedCalls = appUsageViewModel.callLogsMain.value
                        .sortedByDescending { it.date.toLong() }
                        .groupBy { CommonUtil.convertTimestampToDateOnly(it.date.toLong()) }

                    groupedCalls.forEach { (date, calls) ->
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            SmallText.Medium(
                                title = date,
                                textColor = Color(0xFF9AA1B9),
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                            )
                        }

                        items(calls) { callLogModel ->
                            CallLogHistoryInfoBoxNew(callLogModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CallStatItem(
    count: Int,
    label: String,
    iconId: Int,
    iconTint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = iconTint.copy(alpha = 0.1f),
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        SubHeadingText.SemiBold(
            title = count.toString(),
            textColor = Color(0xFF2A3252)
        )

        SmallText.Medium(
            title = label,
            textColor = Color(0xFF9AA1B9)
        )
    }
}

@Composable
fun CallLogHistoryInfoBoxNew(callLogModel: CallLogModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Call type icon
            val (iconRes, iconTint, bgColor) = when (callLogModel.type) {
                "MISSED" -> Triple(
                    R.drawable.call_missed,
                    Color(0xFFFF5252),
                    Color(0xFFFFF0F0)
                )
                "MADE" -> Triple(
                    R.drawable.call_made,
                    Color(0xFF2196F3),
                    Color(0xFFF0F4FF)
                )
                "RECEIVED" -> Triple(
                    R.drawable.call_received,
                    Color(0xFF4CAF50),
                    Color(0xFFF0FFF4)
                )
                else -> Triple(
                    R.drawable.call_missed,
                    Color(0xFF9AA1B9),
                    Color(0xFFF5F7FF)
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = bgColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Call Type",
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Call Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                SubHeadingText.SemiBold(
                    title = callLogModel.name.ifEmpty { "Unknown" },
                    textColor = Color(0xFF2A3252)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmallText.Medium(
                        title = callLogModel.number,
                        textColor = Color(0xFF9AA1B9)
                    )

                    Text(
                        text = "•",
                        color = Color(0xFF9AA1B9),
                        fontSize = 10.ssp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    SmallText.Medium(
                        title = "${callLogModel.duration} sec",
                        textColor = Color(0xFF9AA1B9)
                    )
                }
            }

            // Call time
            Column(
                horizontalAlignment = Alignment.End
            ) {
                val timeOnly = CommonUtil.convertTimestampToTimeOnly(callLogModel.date.toLong())
                RegularText.Medium(
                    title = timeOnly,
                    textColor = Color(0xFF2A3252)
                )
            }
        }
    }
}

// Utility extension functions (add these if you don't have them)
fun CommonUtil.convertTimestampToDateOnly(timestamp: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp

    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance()
    yesterday.add(Calendar.DAY_OF_YEAR, -1)

    return when {
        isSameDay(calendar, today) -> "Today"
        isSameDay(calendar, yesterday) -> "Yesterday"
        else -> {
            val format = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
            format.format(Date(timestamp))
        }
    }
}

fun CommonUtil.convertTimestampToTimeOnly(timestamp: Long): String {
    val format = SimpleDateFormat("h:mm a", Locale.getDefault())
    return format.format(Date(timestamp))
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}



