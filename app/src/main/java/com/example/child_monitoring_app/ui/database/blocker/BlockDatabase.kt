package com.example.child_monitoring_app.ui.database.blocker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BlockEntity::class], version = 1)
abstract class BlockDatabase : RoomDatabase() {
    abstract fun blockDao(): BlockDao

    companion object {
        @Volatile private var INSTANCE: BlockDatabase? = null

        fun getDatabase(context: Context): BlockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BlockDatabase::class.java,
                    "BlockDB"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
