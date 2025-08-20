package com.piashcse.appscheduler.di

import android.content.Context
import androidx.room.Room
import com.piashcse.appscheduler.data.local.AppSchedulerDatabase
import com.piashcse.appscheduler.data.local.dao.ScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppSchedulerDatabase {
        return Room.databaseBuilder(
            context,
            AppSchedulerDatabase::class.java,
            "app_scheduler_database"
        ).build()
    }

    @Provides
    fun provideScheduleDao(database: AppSchedulerDatabase): ScheduleDao =
        database.scheduleDao()
}