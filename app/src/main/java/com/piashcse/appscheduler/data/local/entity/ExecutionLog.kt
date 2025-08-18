package com.piashcse.appscheduler.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ExecutionLog.kt
@Entity(tableName = "execution_logs")
data class ExecutionLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scheduleId: Long,
    val attemptAt: Long = System.currentTimeMillis(),
    val status: String,             // e.g., "SUCCESS", "FAILED_BLOCKED_BY_OS", "APP_NOT_INSTALLED", "NOTIFICATION_SENT"
    val details: String? = null
)