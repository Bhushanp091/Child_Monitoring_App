package com.example.child_monitoring_app.features.app_blocker

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.child_monitoring_app.core.firebase.FirebaseAuthManager
import com.example.child_monitoring_app.core.preference.SharedPreference
import java.util.concurrent.TimeUnit

class UploadAppLaunchWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val username = SharedPreference.getChildId(this.applicationContext)
        if (username.isNullOrEmpty()) {
            Log.e("UploadWorker", "Username is null or empty")
            return Result.failure()
        }
        FirebaseAuthManager().uploadAppLaunchCountFirebase(this.applicationContext, username)
        return Result.success()
    }
}



fun scheduleAppLaunchUploadWorker(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<UploadAppLaunchWorker>(
        1, TimeUnit.MINUTES
    ).build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "AppLaunchUploadWorker",
        ExistingPeriodicWorkPolicy.UPDATE,
        workRequest
    )
}

