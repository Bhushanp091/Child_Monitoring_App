package com.example.child_monitoring_app.features.auth.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.example.child_monitoring_app.R
import com.example.child_monitoring_app.core.common.ToastType
import com.example.child_monitoring_app.core.common.toast
import com.example.child_monitoring_app.core.navigation.Screen
import com.example.child_monitoring_app.features.auth.AuthViewModel
import com.example.child_monitoring_app.features.theme.buttonColor
import kotlinx.coroutines.launch


/*** Child Login Screen Ui ***/
@Composable
fun ChildLoginScreen(
    authViewModel: AuthViewModel,
    onNavigate: (String) -> Unit
)= with(authViewModel) {


    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val image = if (passwordVisible.value)
        R.drawable.eye
    else R.drawable.eye_slash

    LaunchedEffect(isLogIn.value) {
        if (isLogIn.value) {
            onNavigate(Screen.DashBoard.route)
        }
        clearData()
    }

    LaunchedEffect(message.value) {
        if (message.value == "Login successful") {
            onNavigate(Screen.ChildDashBoard.route)
        }
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
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Child Login",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Login to your children phone to get data",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Email TextField
                OutlinedTextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = { Text("Username") },
                    placeholder = { Text("Enter your child username") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Email Icon"
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Password TextField
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
                        // Navigate to forgot password screen
                    }) {
                        Text("Forgot Password?")
                    }
                }

                // Login Button
                Button(
                    onClick = {
                        if (username.value.isNotEmpty() && password.value.isNotEmpty()) {
                            coroutineScope.launch {
                                val result = authViewModel.childLogin(username.value, password.value, context)
                                message.value =
                                    if (result.isSuccess) "Login successful" else "Login failed"
                                context.toast(message.value, ToastType.ERROR)
                            }
                        } else {
                            context.toast("Enter all field", ToastType.ERROR)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor, // Vibrant blue
                        disabledContainerColor = Color(0xFFBBDEFB), // Light blue when disabled
                        contentColor = Color.White,
                        disabledContentColor = Color.White.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Login")
                }
                Text(text = message.value, color = MaterialTheme.colorScheme.error)

            }
        }
    }
}

