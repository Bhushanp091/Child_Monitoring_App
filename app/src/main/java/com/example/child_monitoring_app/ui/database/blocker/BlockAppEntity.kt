package com.example.child_monitoring_app.ui.database.blocker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BlockData")
data class BlockEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String?,
    val packageName: String?,
    val type: String?,
    val launch: Boolean,
    val notification: Boolean,
    val scheduleType: String?,
    val scheduleParams: String?,
    val scheduleDays: String?,
    val profileName: String?,
    val profileStatus: Boolean,
    val text: String?
)
