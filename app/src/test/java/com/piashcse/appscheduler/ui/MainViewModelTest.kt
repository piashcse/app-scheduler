package com.piashcse.appscheduler.ui

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.piashcse.appscheduler.data.repository.ScheduleRepository
import com.piashcse.appscheduler.ui.screen.MainViewModel
import com.piashcse.appscheduler.util.TestData
import com.piashcse.appscheduler.util.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var repository: ScheduleRepository
    private lateinit var application: Application
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        repository = mockk()
        application = mockk(relaxed = true)

        // Mock the flows
        every { repository.getAllSchedules() } returns flowOf(emptyList())

        // Mock static classes if needed
        mockkObject(com.piashcse.appscheduler.utils.AppUtils)
        mockkObject(com.piashcse.appscheduler.utils.AlarmUtils)

        coEvery {
            com.piashcse.appscheduler.utils.AppUtils.getInstalledApps(any())
        } returns emptyList()

        viewModel = MainViewModel(repository, application)
    }

    @After
    fun tearDown() {
        unmockkObject(com.piashcse.appscheduler.utils.AppUtils)
        unmockkObject(com.piashcse.appscheduler.utils.AlarmUtils)
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.showMessage).isFalse()
            assertThat(state.message).isEmpty()
        }
    }

    @Test
    fun `schedules are loaded on initialization`() = runTest {
        // Given
        val testSchedules = TestData.createTestScheduleList()
        every { repository.getAllSchedules() } returns flowOf(testSchedules)

        // When
        val newViewModel = MainViewModel(repository, application)

        // Then
        newViewModel.schedules.test {
            val schedules = awaitItem()
            assertThat(schedules).isEqualTo(testSchedules)
        }
    }

    @Test
    fun `showScheduleDialog updates state correctly`() {
        // Given
        val testApp = TestData.createTestAppInfo()

        // When
        viewModel.showScheduleDialog(testApp)

        // Then
        assertThat(viewModel.showScheduleDialog.value).isTrue()
        assertThat(viewModel.selectedApp.value).isEqualTo(testApp)
        assertThat(viewModel.editingSchedule.value).isNull()
    }

    @Test
    fun `hideScheduleDialog resets state correctly`() {
        // Given
        viewModel.showScheduleDialog(TestData.createTestAppInfo())

        // When
        viewModel.hideScheduleDialog()

        // Then
        assertThat(viewModel.showScheduleDialog.value).isFalse()
        assertThat(viewModel.selectedApp.value).isNull()
        assertThat(viewModel.editingSchedule.value).isNull()
    }

    @Test
    fun `scheduleApp succeeds with no time conflict`() = runTest {
        // Given
        val appInfo = TestData.createTestAppInfo()
        val scheduledTime = System.currentTimeMillis() + 3600000L
        val scheduleId = 123L

        coEvery { repository.checkTimeConflict(scheduledTime) } returns false
        coEvery { repository.insertSchedule(any()) } returns scheduleId
        coEvery {
            com.piashcse.appscheduler.utils.AlarmUtils.scheduleAlarm(
                any(), scheduleId, appInfo.packageName, appInfo.appName, scheduledTime
            )
        } returns Unit

        // When
        viewModel.scheduleApp(appInfo, scheduledTime)

        // Then
        coVerify { repository.insertSchedule(any()) }
        coVerify {
            com.piashcse.appscheduler.utils.AlarmUtils.scheduleAlarm(
                any(), scheduleId, appInfo.packageName, appInfo.appName, scheduledTime
            )
        }

        // Check success message
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.showMessage).isTrue()
            assertThat(state.message).contains("successfully")
        }
    }

    @Test
    fun `scheduleApp fails with time conflict`() = runTest {
        // Given
        val appInfo = TestData.createTestAppInfo()
        val scheduledTime = System.currentTimeMillis() + 3600000L

        coEvery { repository.checkTimeConflict(scheduledTime) } returns true

        // When
        viewModel.scheduleApp(appInfo, scheduledTime)

        // Then
        coVerify(exactly = 0) { repository.insertSchedule(any()) }

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.showMessage).isTrue()
            assertThat(state.message).contains("Time conflict detected")
        }
    }

    @Test
    fun `updateSchedule succeeds with valid parameters`() = runTest {
        // Given
        val schedule = TestData.createTestScheduleEntity()
        val newTime = System.currentTimeMillis() + 7200000L

        coEvery { repository.checkTimeConflict(newTime, schedule.id) } returns false
        coEvery {
            com.piashcse.appscheduler.utils.AlarmUtils.cancelAlarm(any(), schedule.id)
        } returns Unit
        coEvery { repository.updateSchedule(any()) } returns Unit
        coEvery {
            com.piashcse.appscheduler.utils.AlarmUtils.scheduleAlarm(
                any(), schedule.id, schedule.packageName, schedule.appName, newTime
            )
        } returns Unit

        // When
        viewModel.updateSchedule(schedule, newTime)

        // Then
        coVerify { com.piashcse.appscheduler.utils.AlarmUtils.cancelAlarm(any(), schedule.id) }
        coVerify { repository.updateSchedule(any()) }
        coVerify {
            com.piashcse.appscheduler.utils.AlarmUtils.scheduleAlarm(
                any(), schedule.id, schedule.packageName, schedule.appName, newTime
            )
        }
    }

    @Test
    fun `cancelSchedule cancels alarm and updates status`() = runTest {
        // Given
        val schedule = TestData.createTestScheduleEntity()

        coEvery {
            com.piashcse.appscheduler.utils.AlarmUtils.cancelAlarm(any(), schedule.id)
        } returns Unit
        coEvery { repository.updateSchedule(any()) } returns Unit

        // When
        viewModel.cancelSchedule(schedule)

        // Then
        coVerify { com.piashcse.appscheduler.utils.AlarmUtils.cancelAlarm(any(), schedule.id) }
        coVerify {
            repository.updateSchedule(
                match {
                    it.status == com.piashcse.appscheduler.data.model.ScheduleStatus.CANCELLED
                }
            )
        }
    }

    @Test
    fun `deleteSchedule cancels alarm for pending schedules`() = runTest {
        // Given
        val schedule = TestData.createTestScheduleEntity(
            status = com.piashcse.appscheduler.data.model.ScheduleStatus.PENDING
        )

        coEvery {
            com.piashcse.appscheduler.utils.AlarmUtils.cancelAlarm(any(), schedule.id)
        } returns Unit
        coEvery { repository.deleteSchedule(schedule) } returns Unit

        // When
        viewModel.deleteSchedule(schedule)

        // Then
        coVerify { com.piashcse.appscheduler.utils.AlarmUtils.cancelAlarm(any(), schedule.id) }
        coVerify { repository.deleteSchedule(schedule) }
    }

    @Test
    fun `deleteSchedule does not cancel alarm for non-pending schedules`() = runTest {
        // Given
        val schedule = TestData.createTestScheduleEntity(
            status = com.piashcse.appscheduler.data.model.ScheduleStatus.EXECUTED
        )

        coEvery { repository.deleteSchedule(schedule) } returns Unit

        // When
        viewModel.deleteSchedule(schedule)

        // Then
        coVerify(exactly = 0) {
            com.piashcse.appscheduler.utils.AlarmUtils.cancelAlarm(any(), any())
        }
        coVerify { repository.deleteSchedule(schedule) }
    }

    @Test
    fun `clearMessage resets message state`() = runTest {
        // Given - set a message first
        viewModel.scheduleApp(TestData.createTestAppInfo(), System.currentTimeMillis() + 3600000L)

        // When
        viewModel.clearMessage()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.showMessage).isFalse()
            assertThat(state.message).isEmpty()
        }
    }
}