package com.example.child_monitoring_app.workManager

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object BackgroundDataUploader {
    private const val WORK_NAME = "data_upload_worker"

    fun schedulePeriodicUpload(context: Context, intervalMinutes: Long = 15) {
        // Define constraints - you might want to run with or without network
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Only run when connected to network
            .build()

        // Create periodic work request
        val periodicWorkRequest = PeriodicWorkRequestBuilder<DataUploadWorker>(
            intervalMinutes, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        // Schedule the work
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE, // or KEEP if you don't want to replace existing
            periodicWorkRequest
        )
    }

    fun cancelPeriodicUpload(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}