package com.piashcse.appscheduler.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_scheduler_db"
            )
                .fallbackToDestructiveMigration() // optional, for dev
                .build()
            INSTANCE = instance
            instance
        }
    }
}