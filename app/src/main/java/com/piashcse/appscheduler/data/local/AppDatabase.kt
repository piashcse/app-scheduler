package com.piashcse.appscheduler.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.piashcse.appscheduler.data.local.dao.ScheduleDao
import com.piashcse.appscheduler.data.local.entity.ScheduleEntity
import com.piashcse.appscheduler.utils.Converters

@Database(
    entities = [ScheduleEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppSchedulerDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}