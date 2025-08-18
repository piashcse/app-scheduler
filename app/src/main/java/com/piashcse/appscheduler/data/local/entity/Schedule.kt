package com.piashcse.appscheduler.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Schedule.kt
@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,        // target app package
    val scheduledTimeMillis: Long,  // epoch millis at which to start
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val enabled: Boolean = true
)
