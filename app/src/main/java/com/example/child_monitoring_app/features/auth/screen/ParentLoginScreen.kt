package com.example.child_monitoring_app.features.auth.screen

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
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.core.common.CommonLoader
import com.example.child_monitoring_app.features.auth.BiometricAuthUtil
import com.example.child_monitoring_app.core.common.ToastType
import com.example.child_monitoring_app.core.common.toast
import com.example.child_monitoring_app.core.navigation.Screen
import com.example.child_monitoring_app.core.style_guide.Text.RegularText
import com.example.child_monitoring_app.core.style_guide.Text.SmallText
import com.example.child_monitoring_app.core.style_guide.Text.SubHeadingText
import com.example.child_monitoring_app.features.auth.AuthViewModel
import com.example.child_monitoring_app.features.theme.buttonColor
import kotlinx.coroutines.launch


/*** Parent Login Screen Ui***/
@Composable
fun ParentLoginScreen(
    authViewModel: AuthViewModel,
    onLogin:()->Unit,
    onNavigate: (String) -> Unit
) = with(authViewModel) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val biometricAuthUtil = remember { BiometricAuthUtil(context) }
    val isBiometricAvailable = remember { biometricAuthUtil.canAuthenticate() }

    LaunchedEffect(isLogIn.value) {
        if (isLogIn.value) {
            onLogin()
        }
        clearData()
    }

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
                SubHeadingText.SemiBold(title = "Welcome Back")
                SmallText.Medium(title = "Sign in to continue")

                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
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
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Password") },
                    placeholder = { Text("Enter your password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password Icon"
                        )
                    },
                    trailingIcon = {
                        val image = if (passwordVisible.value)
                            R.drawable.eye
                        else R.drawable.eye_slash
                        Icon(
                            painter = painterResource(image),
                            modifier = Modifier.clickable {
                                passwordVisible.value = !passwordVisible.value
                            },
                            contentDescription = "Toggle password visibility"
                        )
                    },
                    visualTransformation = if (passwordVisible.value)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Perform login action
                            keyboardController?.hide()
                        }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Forgot Password
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
                        if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                            coroutineScope.launch {
                                isLoading.value = true
                                val result =
                                    authViewModel.parentLogin(
                                        email.value,
                                        password.value,
                                        context
                                    )
                                message.value = result.getOrDefault("Wrong Credentials")
                                isLoading.value = false
                                context.toast(message.value, ToastType.ERROR)
                                if (message.value != "Wrong Credentials") {
                                    /***BioMetric***/
                                    if (isBiometricAvailable) {
                                        biometricAuthUtil.showBiometricPrompt(
                                            activity = context as FragmentActivity,
                                            onSuccess = {
                                                authStatus.value = "Authentication successful"
//                                                onAuthenticationSuccess()
                                                isLogIn.value = true
                                            },
                                            onError = {
                                                      _, errorMessage ->
                                                authStatus.value =
                                                    "Authentication error: $errorMessage"
                                            },
                                            onFailed = {
                                                authStatus.value =
                                                    "Authentication failed. Please try again."
                                            }
                                        )
                                    } else {
//                                        onNavigate(Screen.ShowChildList.route)
                                        onLogin()
                                        context.toast("Biometric authentication is not available")
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

                // Sign Up Prompt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SmallText.Medium(title = "Don't have an account? ")
                    SmallText.Medium(
                        title = "Register yourself",
                        modifier = Modifier.clickable {
                            onNavigate(Screen.SignUp.route)
                        },
                    )
                }
                Text(text = message.value, color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (isLoading.value) {
        CommonLoader()
    }
}
