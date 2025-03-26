package com.example.child_monitoring_app.ui.presentation.dashBoard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.child_monitoring_app.Screen
import com.example.child_monitoring_app.ui.presentation.component.TopBar
import com.example.child_monitoring_app.ui.presentation.login.AuthViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.ui.data.ChildData
import com.example.child_monitoring_app.ui.presentation.appUsage.AppUsageViewModel

@Composable
fun DashBoardMainScreenOld(
    authViewModel: AuthViewModel,
    navController: NavController
) {
    Scaffold(
        topBar = { TopBar("DashBoard") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddChild.route) }
            ) {
                Icon(Icons.Default.Add, "")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Button(
                onClick = {
                    navController.navigate(Screen.CallHistory.route)
                }
            ) {
                Text("Navigate To CallHistory")
            }

            Button(
                onClick = {
                    navController.navigate(Screen.AppUsage.route)
                }
            ) {
                Text("Navigate To AppUsage")
            }

            Button(
                onClick = {
                    navController.navigate(Screen.ChildDashBoard.route)
                }
            ) {
                Text("Navigate To Show Child Data")
            }

            ChildListScreen(authViewModel)

        }

    }
}


// Feature Model
data class MonitoringFeature(
    val icon: ImageVector,
    val title: String,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoardMainScreen(
    appUsageViewModel: AppUsageViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {



    val features = listOf(
        MonitoringFeature(Icons.Default.PlayArrow, "App Usage", Screen.AppUsage.route),
        MonitoringFeature(Icons.Default.Call, "Call History", Screen.CallHistory.route),
        MonitoringFeature(Icons.Default.AccountBox, "Call Details", Screen.PhoneNumberList.route),
        MonitoringFeature(Icons.Default.PlayArrow, "Browser Details", Screen.BrowserHistory.route),
        MonitoringFeature(Icons.Default.LocationOn, "Location", Screen.LocationScreen.route)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DashBoard",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Children List Section


//
//                // Features Grid
//                Spacer(modifier = Modifier.height(24.dp))
                ChildListScreen(authViewModel)


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

            }
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

