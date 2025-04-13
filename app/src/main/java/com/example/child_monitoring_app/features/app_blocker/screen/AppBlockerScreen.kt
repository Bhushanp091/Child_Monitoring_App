package com.example.child_monitoring_app.features.app_blocker.screen

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.platform.LocalContext
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.core.style_guide.Text.RegularText
import com.example.child_monitoring_app.core.style_guide.Text.SmallText
import com.example.child_monitoring_app.core.style_guide.Text.SubHeadingText
import com.example.child_monitoring_app.features.app_usage.AppUsageInfo
import com.example.child_monitoring_app.features.app_usage.AppUsageViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import network.chaintech.sdpcomposemultiplatform.sdp


@Composable
fun AppBlockerScreen(
    modifier: Modifier,
    appUsageViewModel: AppUsageViewModel
) {

    val allApps = appUsageViewModel.usageData.value
    var selectedApps = remember { mutableStateListOf<AppUsageInfo>() }
    val context = LocalContext.current
    val parentId = SharedPreference.getParentId(context) ?: ""

    LaunchedEffect (Unit){
        appUsageViewModel.firebaseManager.fetchBlockedAppFromFirebase(
            parentId,
            appUsageViewModel.childId.value
        ) { it ->
            appUsageViewModel.blockedApp.value += it
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FF))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AppStatItem(
                count = allApps.size,
                label = "Available",
                iconId = R.drawable.child_image,
                iconTint = Color(0xFF2196F3)
            )

            AppStatItem(
                count = appUsageViewModel.blockedApp.value.size,
                label = "Blocked",
                iconId = R.drawable.child_image,
                iconTint = Color(0xFFFF5252)
            )

            AppStatItem(
                count = (allApps.size + appUsageViewModel.blockedApp.value.size),
                label = "Total Apps",
                iconId = R.drawable.child_image,
                iconTint = Color(0xFF4CAF50)
            )
        }

        // Header for blocked apps
        LazyColumn(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxWidth()
        ) {
            if (appUsageViewModel.blockedApp.value.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SubHeadingText.SemiBold(title = "Blocked Apps")
                        SmallText.Medium(title = "${appUsageViewModel.blockedApp.value.size} apps")
                    }
                }

                items(appUsageViewModel.blockedApp.value) { app ->
                    BlockedAppItemCard(
                        app = app,
                        onUnblock = {
                            appUsageViewModel.blockedApp.value -= app
                        }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SubHeadingText.SemiBold(title = "Available Apps")
                    SmallText.Medium(title = "${allApps.size} apps")
                }
            }


            items(allApps) { app ->
                AppItemCard(
                    app = app,
                    isSelected = selectedApps.contains(app),
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            selectedApps.add(app)
                        } else {
                            selectedApps.remove(app)
                        }
                    }
                )
            }
        }
        AnimatedVisibility(
            visible = selectedApps.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Button(
                onClick = {
                    println("Selected app $selectedApps")
                    appUsageViewModel.blockedApp.value += selectedApps
                    appUsageViewModel.firebaseManager.uploadBlockedAppList(
                        appUsageViewModel.childId.value,
                        selectedApps.toList()
                    ) {
                        selectedApps.clear()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5252)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                RegularText.SemiBold(
                    title = "Block Selected Apps (${selectedApps.size})",
                )
            }
        }
    }
}

@Composable
fun AppStatItem(
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
        SmallText.Bold(title = count.toString())
        SmallText.Medium(title = label)
    }
}

@Composable
fun AppItemCard(
    app: AppUsageInfo,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    val context = LocalContext.current
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
            // App icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = Color(0xFFF5F7FF),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (appIcon != null) {
                    Image(
                        painter = rememberDrawablePainter(drawable = appIcon),
                        contentDescription = "Icon",
                        modifier = Modifier
                            .size(22.sdp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = "Icon",
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .size(22.sdp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // App Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                SubHeadingText.Medium(title = app.appName)
                Spacer(modifier = Modifier.height(2.dp))
                RegularText.Medium(title = app.packageName)

            }

            // Checkbox
            Checkbox(
                checked = isSelected,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF2196F3),
                    uncheckedColor = Color(0xFF9AA1B9)
                )
            )
        }
    }
}

@Composable
fun BlockedAppItemCard(
    app: AppUsageInfo,
    onUnblock: () -> Unit
) {

    val context = LocalContext.current
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
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF0F0)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon with block indicator
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = Color(0xFFFFE0E0),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                if (appIcon != null) {
                    Image(
                        painter = rememberDrawablePainter(drawable = appIcon),
                        contentDescription = "Icon",
                        modifier = Modifier
                            .size(22.sdp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = "Icon",
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .size(22.sdp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // App Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                SubHeadingText.Medium(title = app.appName)
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RegularText.Medium(title = app.packageName)
                }
            }

            IconButton(
                onClick = onUnblock,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFE0E0))
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Unblock ${app.appName}",
                    tint = Color(0xFFFF5252)
                )
            }
        }
    }
}