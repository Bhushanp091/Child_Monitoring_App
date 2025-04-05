package com.example.child_monitoring_app.ui.data

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity

@Composable
fun BiometricScreen(
    onAuthenticationSuccess: () -> Unit = {}
) {
    // Get current context
    val context = LocalContext.current

    // Create biometric utility
    val biometricAuthUtil = remember { BiometricAuthUtil(context) }

    // State to track authentication status
    var authStatus by remember { mutableStateOf("Not authenticated") }

    // Check if biometric authentication is available
    val isBiometricAvailable = remember { biometricAuthUtil.canAuthenticate() }
    val biometricStatus = remember { biometricAuthUtil.getBiometricAvailabilityStatus() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Biometric Authentication",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = biometricStatus,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Status: $authStatus",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (isBiometricAvailable) {
                    biometricAuthUtil.showBiometricPrompt(
                        activity = context as FragmentActivity,
                        onSuccess = {
                            authStatus = "Authentication successful"
                            onAuthenticationSuccess()
                        },
                        onError = { errorCode, errorMessage ->
                            authStatus = "Authentication error: $errorMessage"
                        },
                        onFailed = {
                            authStatus = "Authentication failed. Please try again."
                        }
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Biometric authentication is not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            enabled = isBiometricAvailable
        ) {
            Text("Authenticate")
        }
    }
}