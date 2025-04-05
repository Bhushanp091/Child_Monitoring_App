package com.example.child_monitoring_app.ui.presentation.dashBoard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.child_monitoring_app.Screen
import com.example.child_monitoring_app.ui.data.SharedPreference
import com.example.child_monitoring_app.ui.presentation.component.CommonButton
import com.example.child_monitoring_app.ui.presentation.login.AuthViewModel
import com.google.android.gms.maps.model.LatLng
import network.chaintech.sdpcomposemultiplatform.sdp

@Composable
fun ChildDashBoardScreen(navController: NavController,authViewModel: AuthViewModel) {
    Column (
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 10.sdp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val context = LocalContext.current
        val childId  = SharedPreference.getChildId(context)?:""
        val childLocation = authViewModel.currentLocation.value ?: LatLng(19.076090,72.877426)

        CommonButton(
            text = "Hmmmmmmmmmmmmmmmm :)",
            onClick = {
                authViewModel.storeChildData(context, childLocation,childId)
            }
        )
        Spacer(modifier = Modifier.padding(10.sdp))
        CommonButton(
            text = "Get Location",
            onClick = {
                navController.navigate(Screen.ChildLocationScreen.route)
            }
        )
        Spacer(modifier = Modifier.padding(10.sdp))
        CommonButton(
            text = "Log out Child",
            onClick = {
                SharedPreference.logout(context)
                authViewModel.logOut()
                navController.navigate(Screen.PreLogin.route) {
                    popUpTo(Screen.ChildDashBoard.route) { inclusive = true }
                }
            }
        )
    }
}

