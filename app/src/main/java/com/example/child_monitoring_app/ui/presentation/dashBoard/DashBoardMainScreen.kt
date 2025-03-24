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

@Composable
fun DashBoardMainScreen(
    authViewModel:AuthViewModel,
    navController: NavController
) {

    Scaffold (
        topBar = { TopBar("DashBoard") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {navController.navigate(Screen.AddChild.route)}
            ) {
                Icon(Icons.Default.Add,"")
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
                    navController.navigate(Screen.ShowChildData.route)
                }
            ) {
                Text("Navigate To Show Child Data")
            }

            ChildListScreen(authViewModel)

        }

    }
}