package com.example.child_monitoring_app.workManager


import android.content.Context
import android.location.Location
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.child_monitoring_app.core.firebase.FirebaseAuthManager
import com.example.child_monitoring_app.core.preference.SharedPreference
import com.example.child_monitoring_app.core.preference.SharedPreference.saveBlockedApps
import com.example.child_monitoring_app.core.preference.SharedPreference.saveBlockedWeb
import com.example.child_monitoring_app.features.home.screen.getBatteryPercentage
import com.example.child_monitoring_app.features.network.NetworkStatusTracker
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.first

class DataUploadWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            val childId = SharedPreference.getChildId(context) ?: return Result.failure()
            val parentId = SharedPreference.getParentId(context) ?: return Result.failure()
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
            val lastKnownLocation: Location? = try {
                locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
            } catch (e: SecurityException) {
                null
            }

            val childLocation = lastKnownLocation?.latitude?.let { LatLng(it, lastKnownLocation.longitude) }


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
            if (childLocation != null) {
                println("Current Location $childLocation")
                firebaseManager.uploadChildLocationToFirebase(childId,childLocation)
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
