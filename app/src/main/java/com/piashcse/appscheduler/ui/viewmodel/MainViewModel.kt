package com.piashcse.appscheduler.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.piashcse.appscheduler.data.local.AppSchedulerDatabase
import com.piashcse.appscheduler.data.local.entity.ScheduleEntity
import com.piashcse.appscheduler.data.model.AppInfo
import com.piashcse.appscheduler.data.model.ScheduleStatus
import com.piashcse.appscheduler.data.repository.ScheduleRepository
import com.piashcse.appscheduler.utils.AlarmUtils
import com.piashcse.appscheduler.utils.AppUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppSchedulerDatabase.getDatabase(application)
    private val repository = ScheduleRepository(database.scheduleDao())

    private val _uiState = MutableStateFlow(AppSchedulerUiState())
    val uiState: StateFlow<AppSchedulerUiState> = _uiState.asStateFlow()

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _schedules = MutableStateFlow<List<ScheduleEntity>>(emptyList())
    val schedules: StateFlow<List<ScheduleEntity>> = _schedules.asStateFlow()

    private val _showScheduleDialog = mutableStateOf(false)
    val showScheduleDialog: State<Boolean> = _showScheduleDialog

    private val _selectedApp = mutableStateOf<AppInfo?>(null)
    val selectedApp: State<AppInfo?> = _selectedApp

    private val _editingSchedule = mutableStateOf<ScheduleEntity?>(null)
    val editingSchedule: State<ScheduleEntity?> = _editingSchedule

    // Observer for schedules LiveData
    private val schedulesObserver = Observer<List<ScheduleEntity>> { scheduleList ->
        _schedules.value = scheduleList
    }

    init {
        loadInstalledApps()
        loadSchedules()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            try {
                val apps = AppUtils.getInstalledApps(getApplication())
                _installedApps.value = apps
            } catch (e: Exception) {
                showMessage("Error loading apps: ${e.message}")
            }
        }
    }

    private fun loadSchedules() {
        // Observe LiveData and convert to StateFlow
        repository.getAllSchedules().observeForever(schedulesObserver)
    }

    override fun onCleared() {
        super.onCleared()
        // Remove observer to prevent memory leaks
        repository.getAllSchedules().removeObserver(schedulesObserver)
    }

    fun showScheduleDialog(appInfo: AppInfo? = null, schedule: ScheduleEntity? = null) {
        _selectedApp.value = appInfo
        _editingSchedule.value = schedule
        _showScheduleDialog.value = true
    }

    fun hideScheduleDialog() {
        _showScheduleDialog.value = false
        _selectedApp.value = null
        _editingSchedule.value = null
    }

    fun scheduleApp(appInfo: AppInfo, scheduledTime: Long) {
        viewModelScope.launch {
            try {
                // Validate scheduled time
                if (scheduledTime <= System.currentTimeMillis()) {
                    showMessage("Please select a future time!")
                    return@launch
                }

                // Check for time conflicts
                if (repository.checkTimeConflict(scheduledTime)) {
                    showMessage("Time conflict detected! Please choose a different time.")
                    return@launch
                }

                val schedule = ScheduleEntity(
                    packageName = appInfo.packageName,
                    appName = appInfo.appName,
                    scheduledTime = scheduledTime,
                    status = ScheduleStatus.PENDING
                )

                val scheduleId = repository.insertSchedule(schedule)

                // Set alarm
                AlarmUtils.scheduleAlarm(
                    getApplication(),
                    scheduleId,
                    appInfo.packageName,
                    appInfo.appName,
                    scheduledTime
                )

                showMessage("App scheduled successfully!")
                hideScheduleDialog()
            } catch (e: Exception) {
                showMessage("Error scheduling app: ${e.message}")
            }
        }
    }

    fun updateSchedule(schedule: ScheduleEntity, newTime: Long) {
        viewModelScope.launch {
            try {
                // Validate scheduled time
                if (newTime <= System.currentTimeMillis()) {
                    showMessage("Please select a future time!")
                    return@launch
                }

                // Check for time conflicts excluding current schedule
                if (repository.checkTimeConflict(newTime, schedule.id)) {
                    showMessage("Time conflict detected! Please choose a different time.")
                    return@launch
                }

                // Cancel old alarm
                AlarmUtils.cancelAlarm(getApplication(), schedule.id)

                // Update schedule
                val updatedSchedule = schedule.copy(scheduledTime = newTime)
                repository.updateSchedule(updatedSchedule)

                // Set new alarm
                AlarmUtils.scheduleAlarm(
                    getApplication(),
                    schedule.id,
                    schedule.packageName,
                    schedule.appName,
                    newTime
                )

                showMessage("Schedule updated successfully!")
                hideScheduleDialog()
            } catch (e: Exception) {
                showMessage("Error updating schedule: ${e.message}")
            }
        }
    }

    fun cancelSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            try {
                // Cancel alarm
                AlarmUtils.cancelAlarm(getApplication(), schedule.id)

                // Update status
                val cancelledSchedule = schedule.copy(status = ScheduleStatus.CANCELLED)
                repository.updateSchedule(cancelledSchedule)

                showMessage("Schedule cancelled successfully!")
            } catch (e: Exception) {
                showMessage("Error cancelling schedule: ${e.message}")
            }
        }
    }

    fun deleteSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            try {
                // Cancel alarm if pending
                if (schedule.status == ScheduleStatus.PENDING) {
                    AlarmUtils.cancelAlarm(getApplication(), schedule.id)
                }

                repository.deleteSchedule(schedule)
                showMessage("Schedule deleted successfully!")
            } catch (e: Exception) {
                showMessage("Error deleting schedule: ${e.message}")
            }
        }
    }

    private fun showMessage(message: String) {
        _uiState.value = _uiState.value.copy(
            message = message,
            showMessage = true
        )
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            showMessage = false,
            message = ""
        )
    }
}

data class AppSchedulerUiState(
    val isLoading: Boolean = false,
    val message: String = "",
    val showMessage: Boolean = false
)