package com.example.child_monitoring_app.core.database.AppLaunch

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//@Database(entities = [AppLaunchEntity::class], version = 1)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun appLaunchDao(): AppLaunchDao
//
//    companion object {
//        @Volatile private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "app_launch_db"
//                ).build().also { INSTANCE = it }
//            }
//        }
//    }
//}
