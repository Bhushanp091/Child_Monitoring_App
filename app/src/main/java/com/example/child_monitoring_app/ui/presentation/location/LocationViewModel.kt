package com.example.child_monitoring_app.ui.presentation.location

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.child_monitoring_app.ui.presentation.BaseViewModel
import com.google.android.gms.maps.model.LatLng

class LocationViewModel ():BaseViewModel(){

    val childLocation = mutableStateOf<LatLng?>(null)


}