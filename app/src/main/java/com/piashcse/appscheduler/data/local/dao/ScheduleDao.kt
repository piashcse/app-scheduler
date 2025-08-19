package com.piashcse.appscheduler.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.piashcse.appscheduler.data.local.entity.ScheduleEntity
import com.piashcse.appscheduler.data.model.ScheduleStatus
@Dao
interface ScheduleDao {

    @Query("SELECT * FROM schedules ORDER BY scheduledTime ASC")
    fun getAllSchedules(): LiveData<List<ScheduleEntity>>

    @Query("SELECT * FROM schedules WHERE status = :status ORDER BY scheduledTime ASC")
    fun getSchedulesByStatus(status: ScheduleStatus): LiveData<List<ScheduleEntity>>

    // Added sync version for boot receiver
    @Query("SELECT * FROM schedules WHERE status = :status ORDER BY scheduledTime ASC")
    suspend fun getSchedulesByStatusSync(status: ScheduleStatus): List<ScheduleEntity>

    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getScheduleById(id: Long): ScheduleEntity?

    @Insert
    suspend fun insertSchedule(schedule: ScheduleEntity): Long

    @Update
    suspend fun updateSchedule(schedule: ScheduleEntity)

    @Delete
    suspend fun deleteSchedule(schedule: ScheduleEntity)

    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteScheduleById(id: Long)

    @Query("SELECT * FROM schedules WHERE scheduledTime BETWEEN :startTime AND :endTime AND status = 'PENDING'")
    suspend fun getConflictingSchedules(startTime: Long, endTime: Long): List<ScheduleEntity>
}