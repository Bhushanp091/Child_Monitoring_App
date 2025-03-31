package com.example.child_monitoring_app.ui.presentation.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.child_monitoring_app.Screen
import com.example.child_monitoring_app.ui.presentation.dashBoard.CommonToolbar

@Composable
fun MainScaffold(
    navController: NavHostController,
    title: String,
    showTopBar: Boolean = true,
    showFloatingButton: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            if (showTopBar) {
                CommonToolbar(title = title, onBackClick = { navController.popBackStack() })
            }
        },
        floatingActionButton = {
            if (showFloatingButton) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddChild.route) }
                ) {
                    Icon(Icons.Default.Add, "")
                }
            }
        },
    ) { innerPadding ->
        content(innerPadding)
    }
}
