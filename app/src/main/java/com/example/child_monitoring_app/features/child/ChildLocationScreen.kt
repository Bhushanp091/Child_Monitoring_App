package com.example.child_monitoring_app.features.child

import android.os.Looper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.features.location.LocationViewModel
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
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChildLocationScreen(
    locationViewModel: LocationViewModel
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val childId = SharedPreference.getChildId(context) ?: ""

    val locationRequest = remember {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5 * 60 * 1000L) // every 5 minutes
            .setMinUpdateIntervalMillis(2 * 60 * 1000L) // at least every 2 min if possible
            .build()
    }

    val locationPermissions = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val hasLocationPermissions = locationPermissions.allPermissionsGranted

    // 🔁 Location Callback to receive location updates
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    if (location.latitude != 0.0 && location.longitude != 0.0) {
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
    }

    // 🔐 Request permissions when screen starts
    LaunchedEffect(Unit) {
        if (!hasLocationPermissions) {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }

    // 📍 Start location updates when permission is granted
    DisposableEffect(hasLocationPermissions) {
        if (hasLocationPermissions) {
            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // 🗺️ Show the Map UI
    Box(modifier = Modifier.fillMaxSize()) {
        locationViewModel.currentLocation.value?.let { location ->
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(location, 15f)
            }

            LaunchedEffect(location) {
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
                    title = "Child Location"
                )
            }
        } ?: run {
            Text(
                text = if (hasLocationPermissions) "Getting your location..." else "Location permission is required",
                modifier = Modifier.align(Alignment.Center).padding(16.dp)
            )
        }
    }
}
