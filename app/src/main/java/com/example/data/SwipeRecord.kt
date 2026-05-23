package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "swipe_records")
data class SwipeRecord(
    @PrimaryKey val packageName: String,
    val appName: String,
    val category: String,
    val storageSizeMb: Int,
    val lastUsedDaysAgo: Int,
    val swipeStatus: String, // "PENDING", "KEPT", "QUEUED", "UNINSTALLED"
    val isSystemApp: Boolean = false,
    val swipeTimestamp: Long = 0L
)
