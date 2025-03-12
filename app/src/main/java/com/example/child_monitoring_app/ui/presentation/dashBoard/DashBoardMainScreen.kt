package com.example.child_monitoring_app.ui.presentation.dashBoard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.child_monitoring_app.Screen

@Composable
fun DashBoardMainScreen(
    navController: NavController
) {

    Column(
        modifier = Modifier.fillMaxSize(),
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
                navController.navigate(Screen.CallHistory.route)
            }
        ) {
            Text("Navigate To AppUsage")
        }
    }

}