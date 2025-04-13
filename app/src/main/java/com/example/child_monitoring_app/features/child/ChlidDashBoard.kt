package com.example.child_monitoring_app.features.child

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.core.common.CommonButton
import com.example.child_monitoring_app.core.navigation.Screen
import com.example.child_monitoring_app.core.preference.SharedPreference.saveBlockedApps
import com.example.child_monitoring_app.core.preference.SharedPreference.saveBlockedWeb
import com.example.child_monitoring_app.core.util.areAllPermissionsGranted
import com.example.child_monitoring_app.features.auth.AuthViewModel
import com.example.child_monitoring_app.service.AppBlockerService
import com.google.android.gms.maps.model.LatLng
import network.chaintech.sdpcomposemultiplatform.sdp

@Composable
fun ChildDashBoardScreen(authViewModel: AuthViewModel,onNavigate:(String)->Unit) {

    val context = LocalContext.current
//    LaunchedEffect (Unit){
//        if (!areAllPermissionsGranted(context)){
//            onNavigate(Screen.Permission.route)
//        }
//    }

    Column (
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 10.sdp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val context = LocalContext.current
        val childId  = SharedPreference.getChildId(context)?:""
        val parentId  = SharedPreference.getParentId(context)?:""
        val childLocation = authViewModel.currentLocation.value ?: LatLng(19.076090,72.877426)

        CommonButton(
            text = "Hmmmmmmmmmmmmmmmm :)",
            onClick = {
                authViewModel.storeChildData(context, childLocation,childId)
            }
        )
        Spacer(modifier = Modifier.padding(10.sdp))
        CommonButton(
            text = "Permission",
            onClick = {
                onNavigate(Screen.Permission.route)
            }
        )
        Spacer(modifier = Modifier.padding(10.sdp))
        CommonButton(
            text = "Get Location",
            onClick = {
                onNavigate(Screen.ChildLocationScreen.route)
            }
        )
        Spacer(modifier = Modifier.padding(10.sdp))
        CommonButton(
            text = "Turn on Blocker",
            onClick = {
                authViewModel.firebaseManager.fetchBlockedAppFromFirebase(parentId,childId){ it ->
                    saveBlockedApps(context,it.map { it.packageName })
                }
                authViewModel.firebaseManager.fetchBlockedWebFromFirebase(parentId,childId){ it ->
                    saveBlockedWeb(context,it)
                }
            }
        )
        Spacer(modifier = Modifier.padding(10.sdp))
        CommonButton(
            text = "Log out Child",
            onClick = {
                SharedPreference.logout(context)
                authViewModel.logOut()
                onNavigate(Screen.PreLogin.route)
            }
        )
    }
}

