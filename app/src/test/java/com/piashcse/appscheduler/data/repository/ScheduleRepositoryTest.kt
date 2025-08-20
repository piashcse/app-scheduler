package com.piashcse.appscheduler.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth
import com.piashcse.appscheduler.data.local.dao.ScheduleDao
import com.piashcse.appscheduler.data.model.ScheduleStatus
import com.piashcse.appscheduler.util.TestData
import com.piashcse.appscheduler.util.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ScheduleRepositoryTest {

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var scheduleDao: ScheduleDao
    private lateinit var repository: ScheduleRepository

    @Before
    fun setUp() {
        scheduleDao = mockk()
        repository = ScheduleRepository(scheduleDao)
    }

    @Test
    fun `getAllSchedules returns flow from dao`() = runTest {
        // Given
        val testSchedules = TestData.createTestScheduleList()
        every { scheduleDao.getAllSchedules() } returns flowOf(testSchedules)

        // When & Then
        repository.getAllSchedules().test {
            Truth.assertThat(awaitItem()).isEqualTo(testSchedules)
            awaitComplete()
        }
    }

    @Test
    fun `getSchedulesByStatus returns filtered schedules`() = runTest {
        // Given
        val pendingSchedules =
            listOf(TestData.createTestScheduleEntity(status = ScheduleStatus.PENDING))
        every { scheduleDao.getSchedulesByStatus(ScheduleStatus.PENDING) } returns flowOf(
            pendingSchedules
        )

        // When & Then
        repository.getSchedulesByStatus(ScheduleStatus.PENDING).test {
            Truth.assertThat(awaitItem()).isEqualTo(pendingSchedules)
            awaitComplete()
        }
    }

    @Test
    fun `insertSchedule delegates to dao and returns id`() = runTest {
        // Given
        val schedule = TestData.createTestScheduleEntity()
        val expectedId = 123L
        coEvery { scheduleDao.insertSchedule(schedule) } returns expectedId

        // When
        val result = repository.insertSchedule(schedule)

        // Then
        Truth.assertThat(result).isEqualTo(expectedId)
        coVerify { scheduleDao.insertSchedule(schedule) }
    }

    @Test
    fun `updateSchedule delegates to dao`() = runTest {
        // Given
        val schedule = TestData.createTestScheduleEntity()
        coEvery { scheduleDao.updateSchedule(schedule) } returns Unit

        // When
        repository.updateSchedule(schedule)

        // Then
        coVerify { scheduleDao.updateSchedule(schedule) }
    }

    @Test
    fun `deleteSchedule delegates to dao`() = runTest {
        // Given
        val schedule = TestData.createTestScheduleEntity()
        coEvery { scheduleDao.deleteSchedule(schedule) } returns Unit

        // When
        repository.deleteSchedule(schedule)

        // Then
        coVerify { scheduleDao.deleteSchedule(schedule) }
    }

    @Test
    fun `checkTimeConflict returns true when conflicts exist`() = runTest {
        // Given
        val scheduledTime = System.currentTimeMillis()
        val conflictingSchedules = listOf(TestData.createTestScheduleEntity())
        coEvery {
            scheduleDao.getConflictingSchedules(any(), any())
        } returns conflictingSchedules

        // When
        val hasConflict = repository.checkTimeConflict(scheduledTime)

        // Then
        Truth.assertThat(hasConflict).isTrue()
    }

    @Test
    fun `checkTimeConflict returns false when no conflicts exist`() = runTest {
        // Given
        val scheduledTime = System.currentTimeMillis()
        coEvery {
            scheduleDao.getConflictingSchedules(any(), any())
        } returns emptyList()

        // When
        val hasConflict = repository.checkTimeConflict(scheduledTime)

        // Then
        Truth.assertThat(hasConflict).isFalse()
    }

    @Test
    fun `checkTimeConflict excludes specified schedule ID`() = runTest {
        // Given
        val scheduledTime = System.currentTimeMillis()
        val excludeId = 5L
        val conflictingSchedules = listOf(
            TestData.createTestScheduleEntity(id = excludeId),
            TestData.createTestScheduleEntity(id = 10L)
        )
        coEvery {
            scheduleDao.getConflictingSchedules(any(), any())
        } returns conflictingSchedules

        // When
        val hasConflict = repository.checkTimeConflict(scheduledTime, excludeId)

        // Then
        Truth.assertThat(hasConflict).isTrue() // Still has conflict from ID 10
    }
}