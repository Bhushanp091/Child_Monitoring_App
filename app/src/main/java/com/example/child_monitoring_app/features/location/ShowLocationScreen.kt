package com.example.child_monitoring_app.features.location

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.features.auth.AuthViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState



@Composable
fun ShowLocationScreen(locationViewModel: LocationViewModel, authViewModel: AuthViewModel) {

    val context = LocalContext.current
    val parentId = SharedPreference.getParentId(context) ?: ""

    LaunchedEffect(Unit) {
        locationViewModel.firebaseManager.fetchChildLocationFromFirebase(
            parentId = parentId,
            childUsername =  authViewModel.childId.value
        ) {
            println("Current :Location $it")
            locationViewModel.childLocation.value = it ?: LatLng(19.076090, 72.877426)
        }
    }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            locationViewModel.childLocation.value ?: LatLng(19.076090, 72.877426),
            15f
        )
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(
                position = locationViewModel.childLocation.value ?: LatLng(19.076090, 72.877426)
            ), title = "Child Location"
        )
    }
}


