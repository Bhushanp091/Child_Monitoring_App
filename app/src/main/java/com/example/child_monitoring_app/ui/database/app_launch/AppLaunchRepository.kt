package com.example.child_monitoring_app.ui.database.app_launch

class AppLaunchRepository(private val dao: AppLaunchDao) {
    suspend fun insertOrUpdate(packageName: String, date: String) {
        val launches = dao.getDailyLaunch(date)
        val existing = launches.find { it.packageName == packageName }
        val newLaunch = if (existing != null) {
            existing.copy(launchCount = existing.launchCount + 1)
        } else {
            AppLaunch(packageName, 1, date)
        }
        dao.insert(newLaunch)
    }

    suspend fun getLaunchesByDate(date: String) = dao.getDailyLaunch(date)
    suspend fun getRange(start: String, end: String) = dao.getLaunchCountInRange(start, end)
}
