package com.example.child_monitoring_app.core.ui

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.child_monitoring_app.core.firebase.FirebaseAuthManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch


open class BaseViewModel : ViewModel() {

    val firebaseManager = FirebaseAuthManager()

    var currentLocation = mutableStateOf<LatLng?>(null)
    var childId = mutableStateOf("")
    var childUserName = mutableStateOf("")
    var childName = mutableStateOf("")


    fun storeChildData(
        context: Context,
        location: LatLng,
        childId: String,
        isConnected: Boolean,
        batteryLevel: Int
    ) {
        viewModelScope.launch {
            firebaseManager.uploadCallLogsToFirebase(context,childId)
//            firebaseManager.uploadAppUsageToFirebase(context,childId)
            firebaseManager.uploadContactsToFirebase(context,childId)
            firebaseManager.uploadChildLocationToFirebase(childId,location)
            firebaseManager.uploadBatteryNetworkData(childId,isConnected,batteryLevel)
        }
    }

}