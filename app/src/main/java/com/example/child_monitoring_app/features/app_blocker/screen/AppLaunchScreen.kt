package com.example.child_monitoring_app.features.app_blocker.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.child_monitoring_app.core.navigation.Screen
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.features.app_blocker.AppLaunchModel
import com.example.child_monitoring_app.features.app_usage.AppUsageViewModel

@Composable
fun AppLaunchScreen(
    modifier: Modifier = Modifier,
    appUsageViewModel: AppUsageViewModel
) {

    val context = LocalContext.current
    val parentId = SharedPreference.getParentId(context)?:""
    var appLaunchCount = remember { mutableListOf<AppLaunchModel>() }

    LaunchedEffect (Unit){
        appUsageViewModel.firebaseManager.fetchAppLaunchData(parentId,appUsageViewModel.childId.value){
            appLaunchCount += it
        }
    }


    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn {
            items(appLaunchCount){
                Text(text = it.packageName)
                Text(text = it.count.toString())
            }
        }
        Text(text = "Web Blocker Screen")

    }
}