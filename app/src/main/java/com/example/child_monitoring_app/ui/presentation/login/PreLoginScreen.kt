package com.example.child_monitoring_app.ui.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.child_monitoring_app.Screen
import network.chaintech.sdpcomposemultiplatform.sdp

@Composable
fun ChooseLoginScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.sdp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Select Login Type", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.sdp))

        Button(
            onClick = { navController.navigate(Screen.Login.route) },
            modifier = Modifier.fillMaxWidth().padding(8.sdp)
        ) {
            Text("Login as Parent")
        }

        Button (
            onClick = { navController.navigate(Screen.ChildLogin.route) },
            modifier = Modifier.fillMaxWidth().padding(8.sdp)
        ) {
            Text("Login as Child")
        }
    }
}
