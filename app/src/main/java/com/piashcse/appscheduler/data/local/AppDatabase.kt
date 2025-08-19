package com.piashcse.appscheduler.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    companion object {
        @Volatile
        private var INSTANCE: AppSchedulerDatabase? = null

        fun getDatabase(context: Context): AppSchedulerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppSchedulerDatabase::class.java,
                    "app_scheduler_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}