package com.piashcse.appscheduler.data.repository

import com.piashcse.appscheduler.data.local.dao.ScheduleDao
import com.piashcse.appscheduler.data.local.entity.ScheduleEntity
import com.piashcse.appscheduler.data.model.ScheduleStatus
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val scheduleDao: ScheduleDao) {

    fun getAllSchedules(): Flow<List<ScheduleEntity>> = scheduleDao.getAllSchedules()

    fun getSchedulesByStatus(status: ScheduleStatus): Flow<List<ScheduleEntity>> =
        scheduleDao.getSchedulesByStatus(status)

    suspend fun getScheduleById(id: Long): ScheduleEntity? = scheduleDao.getScheduleById(id)

    suspend fun insertSchedule(schedule: ScheduleEntity): Long = scheduleDao.insertSchedule(schedule)

    suspend fun updateSchedule(schedule: ScheduleEntity) = scheduleDao.updateSchedule(schedule)

    suspend fun deleteSchedule(schedule: ScheduleEntity) = scheduleDao.deleteSchedule(schedule)

    suspend fun deleteScheduleById(id: Long) = scheduleDao.deleteScheduleById(id)

    suspend fun checkTimeConflict(scheduledTime: Long, excludeId: Long = -1): Boolean {
        val conflictWindow = 60000L // 1 minute window
        val startTime = scheduledTime - conflictWindow
        val endTime = scheduledTime + conflictWindow

        val conflicts = scheduleDao.getConflictingSchedules(startTime, endTime)
        return conflicts.any { it.id != excludeId }
    }
}