package com.example.data

import android.content.Context
import androidx.room.withTransaction
import com.example.utils.AppLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SwipeRepository(private val context: Context, private val database: AppSwipeDatabase) {
    private val dao = database.swipeRecordDao()
    private val prefs = context.getSharedPreferences("app_swipe_prefs", Context.MODE_PRIVATE)

    fun isIgnoreSystemApps(): Boolean {
        return prefs.getBoolean("ignore_system_apps", true)
    }

    fun setIgnoreSystemApps(ignore: Boolean) {
        prefs.edit().putBoolean("ignore_system_apps", ignore).apply()
    }

    val allRecordsFlow: Flow<List<SwipeRecord>> = dao.getAllRecordsFlow()
    val pendingRecordsFlow: Flow<List<SwipeRecord>> = dao.getPendingSwipeRecordsFlow()
    val keptRecordsFlow: Flow<List<SwipeRecord>> = dao.getRecordsByStatusFlow("KEPT")
    val queuedRecordsFlow: Flow<List<SwipeRecord>> = dao.getRecordsByStatusFlow("QUEUED")
    val uninstalledRecordsFlow: Flow<List<SwipeRecord>> = dao.getRecordsByStatusFlow("UNINSTALLED")

    /**
     * Initializes the database with the current device launcher apps, ensuring we don't overwrite existing user state.
     */
    suspend fun syncInstalledApps() = withContext(Dispatchers.IO) {
        val installedApps = AppLoader.getInstalledLauncherApps(context)
        dao.insertAll(installedApps)
        
        // Let's also check if any app that was in the database has been uninstalled from the device.
        // For real device apps (non-mock), if they are no longer in `installedApps` list, they might have been uninstalled.
        // We can update their status to "UNINSTALLED" to provide feedback on our success!
        val dbApps = dao.getAllRecords()
        val installedPkgSet = installedApps.map { it.packageName }.toSet()
        
        for (dbApp in dbApps) {
            if (!installedPkgSet.contains(dbApp.packageName)) {
                if (dbApp.swipeStatus == "QUEUED" || dbApp.swipeStatus == "UNINSTALLED") {
                    if (dbApp.swipeStatus != "UNINSTALLED") {
                        dao.updateSwipeStatus(dbApp.packageName, "UNINSTALLED", System.currentTimeMillis())
                    }
                } else {
                    dao.deleteByPackageName(dbApp.packageName)
                }
            }
        }
    }

    suspend fun keepApp(packageName: String) {
        dao.updateSwipeStatus(packageName, "KEPT", System.currentTimeMillis())
    }

    suspend fun queueUninstall(packageName: String) {
        dao.updateSwipeStatus(packageName, "QUEUED", System.currentTimeMillis())
    }

    suspend fun skipApp(packageName: String) {
        // Technically skip is like resetting or ignoring, but let's make a skip action reset to PENDING with 0 timestamp
        dao.updateSwipeStatus(packageName, "PENDING", 0L)
    }

    suspend fun updateCategory(packageName: String, category: String) {
        dao.updateCategory(packageName, category)
    }

    suspend fun undoLastSwipe(): SwipeRecord? {
        // Find latest swiped record with non-zero swipeTimestamp
        val allApps = dao.getAllRecords()
        val latestSwiped = allApps
            .filter { it.swipeTimestamp > 0L && (it.swipeStatus == "KEPT" || it.swipeStatus == "QUEUED") }
            .maxByOrNull { it.swipeTimestamp }

        if (latestSwiped != null) {
            // Restore to PENDING
            dao.updateSwipeStatus(latestSwiped.packageName, "PENDING", 0L)
            return latestSwiped.copy(swipeStatus = "PENDING", swipeTimestamp = 0L)
        }
        return null
    }

    suspend fun resetSwipes() {
        dao.resetSwipes()
    }

    suspend fun deleteAppRecord(packageName: String) {
        // When uninstalled, mark as uninstalled or delete
        dao.updateSwipeStatus(packageName, "UNINSTALLED", System.currentTimeMillis())
    }
}
