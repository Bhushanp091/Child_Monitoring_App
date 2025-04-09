package com.example.child_monitoring_app.ui.database.app_launch

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Entity(tableName = "app_launch_table", primaryKeys = ["packageName", "date"])
data class AppLaunch(
    val packageName: String,
    val launchCount: Int,
    val date: String
)

@Dao
interface AppLaunchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appLaunch: AppLaunch)

    @Query("SELECT * FROM app_launch_table WHERE date = :date")
    suspend fun getDailyLaunch(date: String): List<AppLaunch>

    @Query("SELECT packageName, SUM(launchCount) as launchCount FROM app_launch_table WHERE date BETWEEN :start AND :end GROUP BY packageName")
    suspend fun getLaunchCountInRange(start: String, end: String): List<AppLaunch>
}
