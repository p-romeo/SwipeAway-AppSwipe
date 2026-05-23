package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SwipeRecordDao {
    @Query("SELECT * FROM swipe_records ORDER BY appName ASC")
    fun getAllRecordsFlow(): Flow<List<SwipeRecord>>

    @Query("SELECT * FROM swipe_records ORDER BY appName ASC")
    suspend fun getAllRecords(): List<SwipeRecord>

    @Query("SELECT * FROM swipe_records WHERE swipeStatus = :status ORDER BY swipeTimestamp DESC")
    fun getRecordsByStatusFlow(status: String): Flow<List<SwipeRecord>>

    @Query("SELECT * FROM swipe_records WHERE swipeStatus = 'PENDING' ORDER BY lastUsedDaysAgo DESC")
    fun getPendingSwipeRecordsFlow(): Flow<List<SwipeRecord>>

    @Query("SELECT * FROM swipe_records WHERE packageName = :packageName LIMIT 1")
    suspend fun getRecordByPackageName(packageName: String): SwipeRecord?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(records: List<SwipeRecord>)

    @Update
    suspend fun updateRecord(record: SwipeRecord)

    @Query("UPDATE swipe_records SET swipeStatus = :status, swipeTimestamp = :timestamp WHERE packageName = :packageName")
    suspend fun updateSwipeStatus(packageName: String, status: String, timestamp: Long)

    @Query("UPDATE swipe_records SET category = :category WHERE packageName = :packageName")
    suspend fun updateCategory(packageName: String, category: String)

    @Query("UPDATE swipe_records SET swipeStatus = 'PENDING', swipeTimestamp = 0 WHERE swipeStatus = 'KEPT' OR swipeStatus = 'QUEUED'")
    suspend fun resetSwipes()

    @Query("DELETE FROM swipe_records WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)
}
