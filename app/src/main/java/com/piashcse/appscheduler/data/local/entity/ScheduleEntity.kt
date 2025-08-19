package com.piashcse.appscheduler.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.piashcse.appscheduler.data.model.ScheduleStatus
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val scheduledTime: Long,
    val status: ScheduleStatus,
    val createdAt: Long = System.currentTimeMillis(),
    val executedAt: Long? = null
)