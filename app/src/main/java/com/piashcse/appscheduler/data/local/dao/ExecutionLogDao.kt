package com.piashcse.appscheduler.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.piashcse.appscheduler.data.local.entity.ExecutionLog
import kotlinx.coroutines.flow.Flow

// ExecutionLogDao.kt
@Dao
interface ExecutionLogDao {
    @Insert
    suspend fun insert(log: ExecutionLog): Long

    @Query("SELECT * FROM execution_logs WHERE scheduleId = :scheduleId ORDER BY attemptAt DESC")
    fun logsForSchedule(scheduleId: Long): Flow<List<ExecutionLog>>
}