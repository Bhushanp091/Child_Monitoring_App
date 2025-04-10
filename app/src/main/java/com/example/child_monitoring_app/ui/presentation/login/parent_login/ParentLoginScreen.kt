package com.example.child_monitoring_app.ui.presentation.login.parent_login

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.ui.data.BiometricAuthUtil
import com.example.child_monitoring_app.ui.presentation.component.ToastType
import com.example.child_monitoring_app.ui.presentation.component.toast
import com.example.child_monitoring_app.ui.presentation.login.AuthViewModel
import com.example.child_monitoring_app.ui.theme.buttonColor
import com.example.child_monitoring_app.Screen
import kotlinx.coroutines.launch

@Composable
fun ParentLoginScreen(
    authViewModel: AuthViewModel,
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }

    val biometricAuthUtil = remember { BiometricAuthUtil(context) }
    val isBiometricAvailable = remember { biometricAuthUtil.canAuthenticate() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Sign in to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("Enter your email") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Email Icon")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text("Enter your password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Password Icon")
                    },
                    trailingIcon = {
                        val iconRes = if (passwordVisible) R.drawable.eye else R.drawable.eye_slash
                        Icon(
                            painter = painterResource(id = iconRes),
                            modifier = Modifier.clickable { passwordVisible = !passwordVisible },
                            contentDescription = "Toggle password visibility"
                        )
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    }),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        // Add forgot password nav if needed
                    }) {
                        Text("Forgot Password?")
                    }
                }

                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            coroutineScope.launch {
                                val result = authViewModel.login(email, password, context)
                                message = result.getOrDefault("Wrong Credentials")

                                if (message == "Wrong Credentials") {
                                    context.toast(message, ToastType.ERROR)
                                } else {
                                    // Attempt to send OTP
                                    val otpSent = authViewModel.sendEmailOtp(email, context)
                                    if (otpSent) {
                                        navController.navigate(Screen.OtpVerification.createRoute(email))
                                    } else {
                                        context.toast("Failed to send OTP", ToastType.ERROR)
                                    }

                                    // Optional biometric after OTP
                                    if (isBiometricAvailable) {
                                        biometricAuthUtil.showBiometricPrompt(
                                            activity = context as FragmentActivity,
                                            onSuccess = {
                                                context.toast("Biometric authentication successful")
                                            },
                                            onError = { _, errorMessage ->
                                                context.toast("Biometric error: $errorMessage", ToastType.ERROR)
                                            },
                                            onFailed = {
                                                context.toast("Biometric authentication failed", ToastType.ERROR)
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            context.toast("Enter all fields", ToastType.ERROR)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        disabledContainerColor = Color(0xFFBBDEFB),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Login")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Don't have an account? ")
                    Text(
                        "Register yourself",
                        modifier = Modifier.clickable {
                            navController.navigate("signup_screen")
                        },
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(text = message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
