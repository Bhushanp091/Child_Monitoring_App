package com.example.child_monitoring_app.ui.presentation.login.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.child_monitoring_app.Screen
import com.example.child_monitoring_app.ui.presentation.login.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController,authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isLogIn by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect (isLogIn){
        if (isLogIn){
            navController.navigate(Screen.DashBoard.route)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isSignUp) "Sign Up" else "Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val result = if (isSignUp) authViewModel.signUp(email, password,name)
                    else authViewModel.login(email, password,context)
                    message = result.getOrDefault("Error occurred")
                    if (message != "Error occurred") {
                        isLogIn = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isSignUp) "Sign Up" else "Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            isSignUp = !isSignUp
            navController.navigate(Screen.SignUp.route)
        }) {
            Text(if (isSignUp) "Already have an account? Login" else "Don't have an account? Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}

