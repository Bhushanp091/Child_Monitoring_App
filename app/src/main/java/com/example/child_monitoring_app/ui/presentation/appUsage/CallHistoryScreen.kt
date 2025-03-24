package com.example.child_monitoring_app.ui.presentation.appUsage

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.ui.presentation.component.TopBar
import com.example.child_monitoring_app.ui.presentation.dashBoard.CommonToolbar
import network.chaintech.sdpcomposemultiplatform.sdp
import network.chaintech.sdpcomposemultiplatform.ssp


@Composable
fun CallLogHistoryScreen(
    appUsageViewModel: AppUsageViewModel,
    onBackClick:()->Unit
) {

    val context = LocalContext.current


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            appUsageViewModel.hasPermissionMain.value= isGranted
            if (isGranted) {
                appUsageViewModel.callLogsMain.value = appUsageViewModel.getCallLogs(context)
            }
        }
    )

    LaunchedEffect(Unit) {
        if (context.checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            appUsageViewModel.hasPermissionMain.value = true
            appUsageViewModel.callLogsMain.value = appUsageViewModel.getCallLogs(context)
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CommonToolbar(
            title = "CallLog History",
            onBackClick = onBackClick
        )

        if (! appUsageViewModel.hasPermissionMain.value) {
            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.READ_CALL_LOG)
                }
            ) {
                Text(
                    text = "Enable Permission"
                )
            }

        } else {
            LazyColumn(
                modifier = Modifier
            ) {
                items( appUsageViewModel.callLogsMain.value) {
                    CallLogHistoryInfoBox(it)
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CallHistoryScreen(
//    viewModel: AppUsageViewModel,
//    modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//    val permissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) {
//            viewModel.fetchCallLogs(context)
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
//            == PackageManager.PERMISSION_GRANTED
//        ) {
//            viewModel.fetchCallLogs(context)
//        } else {
//            permissionLauncher.launch(Manifest.permission.READ_CALL_LOG)
//        }
//    }
//
//
//    Scaffold (
//        topBar = { TopBar("Call Log History") },
//        modifier = Modifier.fillMaxSize()
//    ) { paddingValue ->
//        Column(modifier = Modifier.fillMaxSize().padding(paddingValue)) {
//            LazyColumn(modifier = Modifier.padding(16.dp)) {
//                items(viewModel.callLogs) { callLog ->
//                    Text(callLog.toString())
//                }
//            }
//        }
//    }
//}


@Composable
fun CallLogHistoryInfoBox(callLogModel: CallLogModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.sdp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.sdp))
            .padding(12.sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(48.sdp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    when (callLogModel.type) {
                        CallType.MISSED -> R.drawable.icon_phone_missed
                        CallType.MADE -> R.drawable.icon_phone_missed
                        CallType.RECEIVED -> R.drawable.icon_phone_missed
                        CallType.UNKNOWN -> R.drawable.icon_phone_missed
                    }
                ),
                contentDescription = "Call Type",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.sdp)
            )
        }

        Spacer(modifier = Modifier.width(12.sdp))

        // Call Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = callLogModel.name.ifEmpty { "Unknown Caller" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 16.ssp
            )
            Text(
                text = "${callLogModel.number} • ${callLogModel.duration} sec",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.ssp
            )
        }

        // Date
        Text(
            text = callLogModel.date,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.ssp
        )
    }
}
