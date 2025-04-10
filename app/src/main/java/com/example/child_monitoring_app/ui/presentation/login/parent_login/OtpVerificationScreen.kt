package com.example.child_monitoring_app.ui.presentation.login.parent_login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.child_monitoring_app.ui.presentation.login.AuthViewModel
import com.example.child_monitoring_app.Screen


@Composable
fun OtpVerificationScreen(
    email: String,
    navController: NavController,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    var otp by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Enter OTP sent to $email",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { otp = it },
            label = { Text("OTP") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                coroutineScope.launch {
                    val isValid = viewModel.verifyEmailOtp(email, otp)
                    isLoading = false
                    if (isValid) {
                        Toast.makeText(context, "OTP Verified", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.ShowChildList.route) {
                            popUpTo("otp_verification_screen") {
                                inclusive = true
                            }
                        }
                    } else {
                        Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verify OTP")
        }
    }
}
