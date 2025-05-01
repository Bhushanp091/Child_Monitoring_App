package com.example.child_monitoring_app.features.location

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.core.style_guide.Text.SmallText
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

    val childLocation = locationViewModel.childLocation.value

    // Track loading state
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        locationViewModel.firebaseManager.fetchChildLocationFromFirebase(
            parentId = parentId,
            childUsername = authViewModel.childId.value
        ) { location ->
            if (location != null && (location.latitude != 0.0 || location.longitude != 0.0)) {
                locationViewModel.childLocation.value = location
            }
            isLoading = false // Mark loading complete whether or not location is found
        }
    }

    // UI
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            childLocation != null && (childLocation.latitude != 0.0 || childLocation.longitude != 0.0) -> {
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(childLocation, 15f)
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = childLocation),
                        title = "Child Location"
                    )
                }
            }

            else -> {
                SmallText.Medium(
                    title = "No location data available.",
                )
            }
        }
    }
}



