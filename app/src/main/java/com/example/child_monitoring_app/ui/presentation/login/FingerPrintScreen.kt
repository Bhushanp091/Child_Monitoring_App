package com.example.child_monitoring_app.ui.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.ui.data.BiometricAuthUtil
import com.example.child_monitoring_app.ui.presentation.component.CommonButton
import com.example.child_monitoring_app.ui.presentation.component.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import network.chaintech.sdpcomposemultiplatform.sdp

@Composable
fun FingerPrintScreen(
    modifier: Modifier = Modifier,
    onNavigate: () -> Unit
) {
    val context = LocalContext.current
    val biometricAuthUtil = remember { BiometricAuthUtil(context) }
    val isBiometricAvailable = remember { biometricAuthUtil.canAuthenticate() }
    val biometricStatus = remember { biometricAuthUtil.getBiometricAvailabilityStatus() }

    var authStatus by remember { mutableStateOf("Not authenticated") }
    var isAuthenticating by remember { mutableStateOf(false) }
    var authFailed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (isBiometricAvailable) {
            isAuthenticating = true
            biometricAuthUtil.showBiometricPrompt(
                activity = context as FragmentActivity,
                onSuccess = {
                    onNavigate()
                    coroutineScope.launch(Dispatchers.Main) {
                        delay(1500L)
                    }
                    authStatus = "Authentication successful"
                    isAuthenticating = false
                },
                onError = { _, errorMessage ->
                    authStatus = "Error: $errorMessage"
                    isAuthenticating = false
                },
                onFailed = {
                    authStatus = "Authentication failed. Try again."
                    isAuthenticating = false
                    authFailed = true
                }
            )
        } else {
            context.toast("Biometric authentication not available")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.sdp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fingerprint Icon
        Box(
            modifier = Modifier
                .size(120.sdp)
                .clip(CircleShape)
                .background(
                    if (authFailed) MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_fingerprint_24),
                contentDescription = "Fingerprint Icon",
                tint = if (authFailed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.sdp)
            )
        }

        Spacer(modifier = Modifier.height(32.sdp))

        // Status Text
        Text(
            text = authStatus,
            style = MaterialTheme.typography.titleMedium,
            color = if (authFailed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.sdp))

        Text(
            text = biometricStatus,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.sdp))

        // Retry Button
        if (!isAuthenticating && isBiometricAvailable) {
            Button(
                onClick = {
                    authFailed = false
                    isAuthenticating = true
                    biometricAuthUtil.showBiometricPrompt(
                        activity = context as FragmentActivity,
                        onSuccess = {
                            authStatus = "Authentication successful"
                            isAuthenticating = false
                            onNavigate()
                        },
                        onError = { _, errorMessage ->
                            authStatus = "Error: $errorMessage"
                            isAuthenticating = false
                        },
                        onFailed = {
                            authStatus = "Authentication failed. Try again."
                            isAuthenticating = false
                            authFailed = true
                        }
                    )
                }
            ) {
                Text("Retry")
            }
        }

        if (isAuthenticating) {
            Spacer(modifier = Modifier.height(16.sdp))
            CircularProgressIndicator()
        }
    }
}
