package com.piashcse.appscheduler.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class ScheduleStatus {
    PENDING,
    EXECUTED,
    CANCELLED,
    FAILED
}