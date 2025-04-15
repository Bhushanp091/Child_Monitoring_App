package com.example.child_monitoring_app.features.app_blocker.screen

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.core.common.CommonLoader
import com.example.child_monitoring_app.core.navigation.Screen
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.core.style_guide.Text.SubHeadingText
import com.example.child_monitoring_app.features.app_blocker.AppLaunchModel
import com.example.child_monitoring_app.features.app_usage.AppUsageViewModel

@Composable
fun AppLaunchScreen(
    modifier: Modifier = Modifier,
    appUsageViewModel: AppUsageViewModel
) {
    val context = LocalContext.current
    val parentId = SharedPreference.getParentId(context) ?: ""
    var appLaunchCount = remember { mutableStateListOf<AppLaunchModel>() }
    var isLoading by remember { mutableStateOf(true) }

    // Theme colors for consistent design
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface




    LaunchedEffect(Unit) {
        appUsageViewModel.firebaseManager.fetchAppLaunchData(parentId, appUsageViewModel.childId.value) { data ->
            appLaunchCount.clear()
            appLaunchCount.addAll(data)
            isLoading = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading) {
                CommonLoader()
            } else if (appLaunchCount.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No app usage data available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = onSurfaceColor.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(appLaunchCount.sortedByDescending { it.count }) { app ->
                        AppUsageCard(app)
                    }
                }
            }
        }
    }
}

@Composable
fun AppUsageCard(app: AppLaunchModel) {
    val packageManager = LocalContext.current.packageManager
    val context = LocalContext.current

    val appName = try {
        packageManager.getApplicationLabel(packageManager.getApplicationInfo(app.packageName, 0)).toString()
    } catch (e: Exception) {
        app.packageName.split(".").last()
    }


    val appIcon: Drawable? = remember {
        try {
            context.packageManager.getApplicationIcon(app.packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon
            if (appIcon != null) {
                Image(
                    painter = rememberDrawable(drawable = appIcon),
                    contentDescription = "App icon",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = appName.first().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // App name and package
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Launch count
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.count.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun rememberDrawable(drawable: Drawable): BitmapPainter {
    val bitmap = remember {
        Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ).also {
            val canvas = Canvas(it)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
    }

    return BitmapPainter(bitmap.asImageBitmap())
}