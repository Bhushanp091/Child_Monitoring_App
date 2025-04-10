package com.example.child_monitoring_app.features.location

import androidx.compose.runtime.mutableStateOf
import com.example.child_monitoring_app.core.ui.BaseViewModel
import com.google.android.gms.maps.model.LatLng

class LocationViewModel (): BaseViewModel(){

    val childLocation = mutableStateOf<LatLng?>(null)


}