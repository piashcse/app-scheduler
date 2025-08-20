package com.piashcse.appscheduler.di

import com.piashcse.appscheduler.data.local.dao.ScheduleDao
import com.piashcse.appscheduler.data.repository.ScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideScheduleRepository(scheduleDao: ScheduleDao): ScheduleRepository {
        return ScheduleRepository(scheduleDao)
    }
}