package com.example.child_monitoring_app.ui.presentation.dashBoard

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.child_monitoring_app.Screen
import com.example.child_monitoring_app.ui.presentation.login.AuthViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.ui.data.SharedPreference
import com.example.child_monitoring_app.ui.presentation.appUsage.AppLaunchScreen
import com.example.child_monitoring_app.ui.presentation.appUsage.AppUsageViewModel
import com.example.child_monitoring_app.ui.presentation.component.CommonButton
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Wifi







// Feature Model
data class MonitoringFeature(
    val icon: ImageVector,
    val title: String,
    val route: String
)

@Composable
fun DashBoardMainScreen(
    modifier: Modifier,
    appUsageViewModel: AppUsageViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    val features = listOf(
        MonitoringFeature(Icons.Default.PlayArrow, "App Usage", Screen.AppUsage.route),
        MonitoringFeature(Icons.Default.Call, "Call History", Screen.CallHistory.route),
        MonitoringFeature(Icons.Default.AccountBox, "Call Details", Screen.PhoneNumberList.route),
        MonitoringFeature(Icons.Default.PlayArrow, "Browser Details", Screen.BrowserHistory.route),
        MonitoringFeature(Icons.Default.LocationOn, "Location", Screen.LocationScreen.route),
        MonitoringFeature(Icons.Default.BatteryStd, "Battery", Screen.Battery.route),
        MonitoringFeature(Icons.Default.Wifi, "Network", Screen.NetworkStatus.route)

    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Monitoring Features",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(features) { feature ->
                    FeatureCard(feature) {
                        navController.navigate(feature.route)
                    }
                }

            }

//            AppLaunchScreen()

            CommonButton(
                text = "Log Out",
                onClick = {
                    SharedPreference.logout(context)
                    authViewModel.logOut()
                    navController.navigate(Screen.PreLogin.route) {
                        popUpTo(Screen.DashBoard.route) { inclusive = true }
                    }
                }
            )

        }


    }
}


@Composable
fun FeatureCard(feature: MonitoringFeature, onClick: (MonitoringFeature) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick(feature) },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = feature.title,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

