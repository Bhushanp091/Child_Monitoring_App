package com.example.child_monitoring_app.core.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.child_monitoring_app.core.navigation.Screen

@Composable
fun MainScaffold(
    navController: NavHostController,
    title: String,
    showBackButton: Boolean = true,
    showFloatingButton: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CommonToolbar(
                title = title,
                showBackButton = showBackButton,
                onBackClick = { navController.popBackStack() })

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
