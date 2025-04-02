package com.example.child_monitoring_app.ui.presentation.dashBoard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.child_monitoring_app.ui.data.SharedPreference
import com.example.child_monitoring_app.ui.presentation.component.CommonButton
import com.example.child_monitoring_app.ui.presentation.login.AuthViewModel

@Composable
fun ChildDashBoardScreen(authViewModel: AuthViewModel) {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val context = LocalContext.current
        val childId  = SharedPreference.getChildId(context)?:""
        CommonButton(
            text = "Hmmmmmmmmmmmmmmmm :)",
            onClick = {
                println("Firebase store child data")
                authViewModel.storeChildData(context,childId)
            }
        )
    }
}

