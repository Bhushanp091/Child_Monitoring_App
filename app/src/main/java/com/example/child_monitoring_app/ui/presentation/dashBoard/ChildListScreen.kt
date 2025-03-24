package com.example.child_monitoring_app.ui.presentation.dashBoard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.ui.data.ChildData
import com.example.child_monitoring_app.ui.data.FirebaseAuthManager
import com.example.child_monitoring_app.ui.presentation.login.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ChildListScreen(
    authViewModel: AuthViewModel
) {
    var children by remember { mutableStateOf<List<ChildData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
               children =  authViewModel.getChildList()
                println("Child List $children")
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.message?:"Error Occurred"
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn {
            items(children) { child ->
                println("Child Data $child")
                Card (
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column (modifier = Modifier.padding(16.dp)) {
                        Text(text = "Name: ${child.name}", )
                        Text(text = "Username: ${child.username}",)
                        Text(text = "Age: ${child.age}",)
                    }
                }
            }
        }
    }
}
