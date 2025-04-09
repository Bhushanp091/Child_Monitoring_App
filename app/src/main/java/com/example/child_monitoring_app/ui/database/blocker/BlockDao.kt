package com.example.child_monitoring_app.ui.database.blocker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: BlockEntity)

    @Query("SELECT * FROM BlockData WHERE packageName = :packageName")
    suspend fun getBlocksByPackage(packageName: String): List<BlockEntity>

    @Query("SELECT * FROM BlockData WHERE name = :url AND type = 'web'")
    suspend fun getWebRecords(url: String): List<BlockEntity>

    @Query("SELECT * FROM BlockData WHERE name = :key AND type = 'key'")
    suspend fun getKeyRecords(key: String): List<BlockEntity>

    @Query("SELECT * FROM BlockData WHERE packageName = :packageName AND type = 'internet'")
    suspend fun getInternetRecords(packageName: String): List<BlockEntity>

    @Query("SELECT * FROM BlockData WHERE type = 'web' AND profileName IS NULL")
    suspend fun getAllWebRecords(): List<BlockEntity>

    @Query("SELECT * FROM BlockData WHERE type = 'key' AND profileName IS NULL")
    suspend fun getAllKeyRecords(): List<BlockEntity>

    @Query("SELECT DISTINCT profileName, profileStatus FROM BlockData WHERE type = 'profile'")
    suspend fun getAllProfiles(): List<ProfileOnly>

    @Query("SELECT * FROM BlockData WHERE profileName = :profileName")
    suspend fun getProfileSchedule(profileName: String): List<BlockEntity>

    @Query("SELECT * FROM BlockData WHERE profileName = :profileName AND type = 'app'")
    suspend fun getProfileApps(profileName: String): List<BlockEntity>

    @Query("SELECT * FROM BlockData WHERE profileName = :profileName AND type = 'web'")
    suspend fun getProfileWebs(profileName: String): List<BlockEntity>

    @Query("SELECT * FROM BlockData WHERE profileName = :profileName AND type = 'key'")
    suspend fun getProfileKeys(profileName: String): List<BlockEntity>
}

data class ProfileOnly(
    val profileName: String?,
    val profileStatus: Boolean
)
