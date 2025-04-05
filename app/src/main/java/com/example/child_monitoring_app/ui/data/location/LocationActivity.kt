package com.example.child_monitoring_app.ui.data.location


import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class LocationMapActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LocationMapScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun LocationMapScreen() {
        // Get the context
        val context = LocalContext.current

        // Initialize location client
        val fusedLocationClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }

        // State to hold current location
        var currentLocation by remember { mutableStateOf<LatLng?>(null) }

        // Location request settings
        val locationRequest = remember {
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(2000)
                .build()
        }

        // Permission handling
        val locationPermissions = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        val hasLocationPermissions = locationPermissions.allPermissionsGranted

        // Location callback
        val locationCallback = remember {
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        currentLocation = LatLng(location.latitude, location.longitude)
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
                            currentLocation = LatLng(it.latitude, it.longitude)
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
            currentLocation?.let { location ->
                // Google Map View
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
//                        position = location,
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
}