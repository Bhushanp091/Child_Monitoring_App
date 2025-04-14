package com.example.child_monitoring_app.core.database.AppLaunch

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

//@Entity(tableName = "app_launches")
//data class AppLaunchEntity(
//    @PrimaryKey val packageName: String,
//    val launchCount: Int
//)
//
//@Dao
//interface AppLaunchDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertLaunch(appLaunchEntity: AppLaunchEntity)
//
//    @Query("SELECT * FROM app_launches")
//    suspend fun getAllLaunches(): List<AppLaunchEntity>
//
//    @Query("DELETE FROM app_launches")
//    suspend fun clearAll()
//}
