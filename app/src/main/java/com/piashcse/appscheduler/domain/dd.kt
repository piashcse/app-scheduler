package com.piashcse.appscheduler.domain

import com.piashcse.appscheduler.data.local.AppDatabase
import com.piashcse.appscheduler.data.local.entity.ExecutionLog
import com.piashcse.appscheduler.data.local.entity.Schedule
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val db: AppDatabase) {
    fun getAllSchedulesFlow(): Flow<List<Schedule>> = db.scheduleDao().getAllFlow()
    suspend fun getSchedule(id: Long) = db.scheduleDao().getById(id)
    fun logsForSchedule(scheduleId: Long): Flow<List<ExecutionLog>> = db.executionLogDao().logsForSchedule(scheduleId)
}