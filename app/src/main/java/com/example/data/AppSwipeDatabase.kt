package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SwipeRecord::class], version = 1, exportSchema = false)
abstract class AppSwipeDatabase : RoomDatabase() {
    abstract fun swipeRecordDao(): SwipeRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppSwipeDatabase? = null

        fun getDatabase(context: Context): AppSwipeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppSwipeDatabase::class.java,
                    "app_swipe_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
