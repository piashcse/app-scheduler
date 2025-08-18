package com.piashcse.appscheduler.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.piashcse.appscheduler.data.local.entity.Schedule
import kotlinx.coroutines.flow.Flow

// ScheduleDao.kt
@Dao
interface ScheduleDao {
    @Insert
    suspend fun insert(schedule: Schedule): Long

    @Update
    suspend fun update(schedule: Schedule)

    @Delete
    suspend fun delete(schedule: Schedule)

    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getById(id: Long): Schedule?

    @Query("SELECT * FROM schedules WHERE scheduledTimeMillis = :time")
    suspend fun getByExactTime(time: Long): List<Schedule>

    @Query("SELECT * FROM schedules ORDER BY scheduledTimeMillis")
    fun getAllFlow(): Flow<List<Schedule>>
}