package com.example.child_monitoring_app.ui.presentation.dashBoard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.child_monitoring_app.ui.presentation.component.CommonButton
import com.example.child_monitoring_app.ui.presentation.login.AuthViewModel

@Composable
fun ChildDashBoardScreen(authViewModel: AuthViewModel) {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("This is child Home screen here all data will be collected :)")
        CommonButton(
            text = "Nacho",
            onClick = {

            }
        )

        GetUserScreen(authViewModel)

    }
}