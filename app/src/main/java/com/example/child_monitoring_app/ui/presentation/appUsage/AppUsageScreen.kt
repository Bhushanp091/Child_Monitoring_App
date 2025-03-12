package com.example.child_monitoring_app.ui.presentation.appUsage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.child_monitoring_app.ui.presentation.component.TopBar

@Composable
fun AppUsageScreen(viewModel: AppUsageViewModel) {

    val appUsageData = viewModel.appUsageData

    val isLoading = viewModel.isLoading
    val currentTimeFrame = viewModel.currentTimeFrame
    val totalScreenTime = viewModel.getTotalScreenTime()

    Scaffold (
        topBar = { TopBar("AppUsage") },
        modifier = Modifier.fillMaxSize()
    ){paddingValue->

        Column(
            modifier = Modifier
                .padding(paddingValue)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "App Usage Stats",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total screen time: $totalScreenTime",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            TimeFrameSelector(
                currentTimeFrame = currentTimeFrame,
                onTimeFrameSelected = { viewModel.setTimeFrame(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(appUsageData) { appUsage ->
                        AppUsageItem(
                            appUsage = appUsage,
                            totalTime = appUsageData.sumOf { it.usageTime },
                            formatTime = { viewModel.formatTime(it) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeFrameSelector(
    currentTimeFrame: TimeFrame,
    onTimeFrameSelected: (TimeFrame) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TimeFrame.values().forEach { timeFrame ->
            val selected = timeFrame == currentTimeFrame
            FilterChip(
                selected = selected,
                onClick = { onTimeFrameSelected(timeFrame) },
                label = {
                    Text(
                        text = timeFrame.name.lowercase().capitalize(),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun AppUsageItem(
    appUsage: AppUsageData,
    totalTime: Long,
    formatTime: (Long) -> String
) {
    val usagePercentage = (appUsage.usageTime.toFloat() / totalTime) * 100

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            appUsage.appIcon?.let { drawable ->
                AppIcon(drawable = drawable)
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appUsage.appName,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { usagePercentage / 100 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = formatTime(appUsage.usageTime),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AppIcon(drawable: Drawable, modifier: Modifier = Modifier) {
    val bitmap = remember(drawable) {
        try {
            drawable.toBitmap(width = 48, height = 48)
        } catch (e: Exception) {
            Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888).apply {
                val canvas = android.graphics.Canvas(this)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
            }
        }
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "App icon",
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
    )
}


