package com.piashcse.appscheduler.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.piashcse.appscheduler.data.local.dao.ExecutionLogDao
import com.piashcse.appscheduler.data.local.dao.ScheduleDao
import com.piashcse.appscheduler.data.local.entity.ExecutionLog
import com.piashcse.appscheduler.data.local.entity.Schedule

// AppDatabase.kt
@Database(entities = [Schedule::class, ExecutionLog::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun executionLogDao(): ExecutionLogDao
}