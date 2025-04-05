package com.example.child_monitoring_app.ui.presentation.location

import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.ui.data.SharedPreference
import com.example.child_monitoring_app.ui.presentation.login.AuthViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ShowLocationScreen(
    locationViewModel: LocationViewModel
) {
    val context = LocalContext.current

    val childId = SharedPreference.getChildId(context) ?: ""


    // Initialize location client
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // State to hold current location

    // Location request settings
    val locationRequest = remember {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30 * 60 * 1000L) // 30 minutes
            .setMinUpdateIntervalMillis(10 * 60 * 1000L) // Minimum 10 minutes
            .build()
    }

    // Permission handling
    val locationPermissions = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val hasLocationPermissions = locationPermissions.allPermissionsGranted

    // Location callback
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val newLatLng = LatLng(location.latitude, location.longitude)
                    if (locationViewModel.currentLocation.value != newLatLng) {
                        locationViewModel.currentLocation.value = newLatLng

                        locationViewModel.firebaseManager.uploadChildLocationToFirebase(
                            childId = childId,
                            location = newLatLng
                        )
                    }
                }
            }

        }
    }

    // Request permissions if needed
    LaunchedEffect(key1 = Unit) {
        if (!hasLocationPermissions) {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }

    // Start location updates
    DisposableEffect(key1 = hasLocationPermissions) {
        if (hasLocationPermissions) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        locationViewModel.currentLocation.value = LatLng(it.latitude, it.longitude)
                    }
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                // Handle exception
            }
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // Show the map or loading
    Box(modifier = Modifier.fillMaxSize()) {
        locationViewModel.currentLocation.value?.let { location ->
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(location, 15f)
            }

            LaunchedEffect(key1 = location) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(location, 15f)
                )
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermissions)
            ) {
                Marker(
//                    position = location,
                    title = "Current Location"
                )
            }
        } ?: run {
            if (hasLocationPermissions) {
                Text(
                    text = "Getting your location...",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Text(
                    text = "Location permission is required",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
        }
    }
}


@Composable
fun ChildLocationMap(locationViewModel: LocationViewModel,authViewModel: AuthViewModel) {

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


