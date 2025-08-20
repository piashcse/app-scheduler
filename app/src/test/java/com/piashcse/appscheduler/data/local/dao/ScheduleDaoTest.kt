package com.piashcse.appscheduler.data.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.piashcse.appscheduler.data.local.AppSchedulerDatabase
import com.piashcse.appscheduler.data.model.ScheduleStatus
import com.piashcse.appscheduler.util.TestData
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScheduleDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppSchedulerDatabase
    private lateinit var scheduleDao: ScheduleDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppSchedulerDatabase::class.java
        ).allowMainThreadQueries().build()

        scheduleDao = database.scheduleDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetSchedule() = runTest {
        // Given
        val schedule = TestData.createTestScheduleEntity(id = 0) // Auto-generate ID

        // When
        val id = scheduleDao.insertSchedule(schedule)
        val retrievedSchedule = scheduleDao.getScheduleById(id)

        // Then
        assertThat(retrievedSchedule).isNotNull()
        assertThat(retrievedSchedule?.packageName).isEqualTo(schedule.packageName)
        assertThat(retrievedSchedule?.appName).isEqualTo(schedule.appName)
        assertThat(retrievedSchedule?.status).isEqualTo(schedule.status)
    }

    @Test
    fun getAllSchedules() = runTest {
        // Given
        val schedule1 = TestData.createTestScheduleEntity(id = 0, packageName = "com.app1")
        val schedule2 = TestData.createTestScheduleEntity(id = 0, packageName = "com.app2")

        scheduleDao.insertSchedule(schedule1)
        scheduleDao.insertSchedule(schedule2)

        // When & Then
        scheduleDao.getAllSchedules().test {
            val schedules = awaitItem()
            assertThat(schedules).hasSize(2)
            assertThat(schedules.map { it.packageName }).containsExactly("com.app1", "com.app2")
        }
    }

    @Test
    fun getSchedulesByStatus() = runTest {
        // Given
        val pendingSchedule = TestData.createTestScheduleEntity(
            id = 0,
            packageName = "com.pending",
            status = ScheduleStatus.PENDING
        )
        val executedSchedule = TestData.createTestScheduleEntity(
            id = 0,
            packageName = "com.executed",
            status = ScheduleStatus.EXECUTED
        )

        scheduleDao.insertSchedule(pendingSchedule)
        scheduleDao.insertSchedule(executedSchedule)

        // When & Then
        scheduleDao.getSchedulesByStatus(ScheduleStatus.PENDING).test {
            val schedules = awaitItem()
            assertThat(schedules).hasSize(1)
            assertThat(schedules[0].packageName).isEqualTo("com.pending")
        }
    }

    @Test
    fun updateSchedule() = runTest {
        // Given
        val schedule = TestData.createTestScheduleEntity(id = 0)
        val id = scheduleDao.insertSchedule(schedule)
        val insertedSchedule = scheduleDao.getScheduleById(id)!!

        // When
        val updatedSchedule = insertedSchedule.copy(status = ScheduleStatus.EXECUTED)
        scheduleDao.updateSchedule(updatedSchedule)

        // Then
        val retrievedSchedule = scheduleDao.getScheduleById(id)
        assertThat(retrievedSchedule?.status).isEqualTo(ScheduleStatus.EXECUTED)
    }

    @Test
    fun deleteSchedule() = runTest {
        // Given
        val schedule = TestData.createTestScheduleEntity(id = 0)
        val id = scheduleDao.insertSchedule(schedule)
        val insertedSchedule = scheduleDao.getScheduleById(id)!!

        // When
        scheduleDao.deleteSchedule(insertedSchedule)

        // Then
        val retrievedSchedule = scheduleDao.getScheduleById(id)
        assertThat(retrievedSchedule).isNull()
    }

    @Test
    fun deleteScheduleById() = runTest {
        // Given
        val schedule = TestData.createTestScheduleEntity(id = 0)
        val id = scheduleDao.insertSchedule(schedule)

        // When
        scheduleDao.deleteScheduleById(id)

        // Then
        val retrievedSchedule = scheduleDao.getScheduleById(id)
        assertThat(retrievedSchedule).isNull()
    }

    @Test
    fun getConflictingSchedules() = runTest {
        // Given
        val baseTime = System.currentTimeMillis()
        val schedule1 = TestData.createTestScheduleEntity(
            id = 0,
            scheduledTime = baseTime,
            packageName = "com.app1"
        )
        val schedule2 = TestData.createTestScheduleEntity(
            id = 0,
            scheduledTime = baseTime + 30000, // 30 seconds later
            packageName = "com.app2"
        )
        val schedule3 = TestData.createTestScheduleEntity(
            id = 0,
            scheduledTime = baseTime + 120000, // 2 minutes later
            packageName = "com.app3"
        )

        scheduleDao.insertSchedule(schedule1)
        scheduleDao.insertSchedule(schedule2)
        scheduleDao.insertSchedule(schedule3)

        // When - search for conflicts within 1 minute window
        val startTime = baseTime - 60000
        val endTime = baseTime + 60000
        val conflicts = scheduleDao.getConflictingSchedules(startTime, endTime)

        // Then
        assertThat(conflicts).hasSize(2) // schedule1 and schedule2
        assertThat(conflicts.map { it.packageName }).containsExactly("com.app1", "com.app2")
    }
}