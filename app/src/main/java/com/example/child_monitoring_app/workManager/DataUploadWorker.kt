package com.example.child_monitoring_app.workManager


import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.child_monitoring_app.core.firebase.FirebaseAuthManager
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.core.preference.SharedPreference.saveBlockedApps
import com.example.child_monitoring_app.core.preference.SharedPreference.saveBlockedWeb
import com.example.child_monitoring_app.features.home.screen.getBatteryPercentage
import com.example.child_monitoring_app.features.network.NetworkStatusTracker
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.first

class DataUploadWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun doWork(): Result {
        try {
            val childId = SharedPreference.getChildId(context) ?: return Result.failure()
            val parentId = SharedPreference.getParentId(context) ?: return Result.failure()
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).build()




//            val childLocation = lastKnownLocation?.latitude?.let { LatLng(it, lastKnownLocation.longitude) }


            val networkStatusTracker = NetworkStatusTracker(context)
            val isConnected = networkStatusTracker.networkStatus.first()
            val batteryLevel = getBatteryPercentage(context)

            val firebaseManager = FirebaseAuthManager()

            firebaseManager.uploadCallLogsToFirebase(context,childId)
            firebaseManager.uploadAppUsageToFirebase(context,childId,"daily")
            firebaseManager.uploadAppUsageToFirebase(context,childId,"weekly")
            firebaseManager.uploadAppUsageToFirebase(context,childId,"monthly")
            firebaseManager.uploadAppLaunchCountFirebase(context,childId)
            firebaseManager.uploadContactsToFirebase(context,childId)

            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val current = LatLng(location.latitude, location.longitude)
                        println("Location $current")
                        firebaseManager.uploadChildLocationToFirebase(childId,current)
                    }
                }

            firebaseManager.uploadBatteryNetworkData(childId,isConnected,batteryLevel)

            firebaseManager.fetchBlockedAppFromFirebase(parentId,childId){ it ->
                saveBlockedApps(context,it.map { it.packageName })
            }
            firebaseManager.fetchBlockedWebFromFirebase(parentId,childId){ it ->
                saveBlockedWeb(context,it)
            }

            return Result.success()

        } catch (e: Exception) {
            println("WorkManager Exception $e")
            return Result.failure(workDataOf("error" to e.message))
        }
    }
}
