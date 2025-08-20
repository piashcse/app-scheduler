package com.piashcse.appscheduler.util

import com.piashcse.appscheduler.data.local.entity.ScheduleEntity
import com.piashcse.appscheduler.data.model.AppInfo
import com.piashcse.appscheduler.data.model.ScheduleStatus

object TestData {

    fun createTestAppInfo(
        packageName: String = "com.test.app",
        appName: String = "Test App"
    ) = AppInfo(
        packageName = packageName,
        appName = appName,
        icon = null
    )

    fun createTestScheduleEntity(
        id: Long = 1L,
        packageName: String = "com.test.app",
        appName: String = "Test App",
        status: ScheduleStatus = ScheduleStatus.PENDING,
        scheduledTime: Long = System.currentTimeMillis() + 3600000, // 1 hour from now
        createdAt: Long = System.currentTimeMillis(),
        executedAt: Long? = null
    ) = ScheduleEntity(
        id = id,
        packageName = packageName,
        appName = appName,
        scheduledTime = scheduledTime,
        status = status,
        createdAt = createdAt,
        executedAt = executedAt
    )

    fun createTestScheduleList() = listOf(
        createTestScheduleEntity(1L, "com.app1", "App 1", ScheduleStatus.PENDING),
        createTestScheduleEntity(2L, "com.app2", "App 2", ScheduleStatus.EXECUTED),
        createTestScheduleEntity(3L, "com.app3", "App 3", ScheduleStatus.FAILED)
    )
}