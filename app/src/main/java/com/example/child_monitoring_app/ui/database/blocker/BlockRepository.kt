package com.example.child_monitoring_app.ui.database.blocker

class BlockRepository(private val dao: BlockDao) {
    suspend fun addBlock(block: BlockEntity) = dao.insertBlock(block)
    suspend fun getWebBlocks(url: String) = dao.getWebRecords(url)
    suspend fun getAllWebBlocks() = dao.getAllWebRecords()
    suspend fun getProfileApps(profileName: String) = dao.getProfileApps(profileName)
    // Add other methods similarly
}
