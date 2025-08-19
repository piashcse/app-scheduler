package com.piashcse.appscheduler.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.piashcse.appscheduler.data.local.entity.ScheduleEntity
import com.piashcse.appscheduler.data.model.AppInfo
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleDialog(
    selectedApp: AppInfo?,
    editingSchedule: ScheduleEntity?,
    onSchedule: (AppInfo, Long) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var selectedDate by remember {
        mutableStateOf(
            if (editingSchedule != null) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = editingSchedule.scheduledTime
                LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            } else {
                LocalDate.now()
            }
        )
    }

    var selectedTime by remember {
        mutableStateOf(
            if (editingSchedule != null) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = editingSchedule.scheduledTime
                LocalTime.of(
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE)
                )
            } else {
                LocalTime.now().plusMinutes(1)
            }
        )
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    // Calculate if the selected time is in the future
    val isValidTime = remember(selectedDate, selectedTime) {
        val calendar = Calendar.getInstance()
        calendar.set(
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth,
            selectedTime.hour,
            selectedTime.minute,
            0
        )
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.timeInMillis > System.currentTimeMillis()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = if (editingSchedule != null) "Edit Schedule" else "Schedule App",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val appName = selectedApp?.appName ?: editingSchedule?.appName ?: ""
                Text(
                    text = "App: $appName",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedButton(
                    onClick = { dateDialogState.show() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text("Date: $selectedDate")
                }

                OutlinedButton(
                    onClick = { timeDialogState.show() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text("Time: $selectedTime")
                }

                if (!isValidTime) {
                    Text(
                        text = "Please select a future time",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            calendar.set(
                                selectedDate.year,
                                selectedDate.monthValue - 1,
                                selectedDate.dayOfMonth,
                                selectedTime.hour,
                                selectedTime.minute,
                                0
                            )
                            calendar.set(Calendar.MILLISECOND, 0)

                            val scheduledTime = calendar.timeInMillis

                            if (selectedApp != null) {
                                onSchedule(selectedApp, scheduledTime)
                            } else if (editingSchedule != null) {
                                // For editing, we need to pass the app info
                                val appInfo = AppInfo(
                                    packageName = editingSchedule.packageName,
                                    appName = editingSchedule.appName,
                                    icon = null
                                )
                                onSchedule(appInfo, scheduledTime)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = (selectedApp != null || editingSchedule != null) && isValidTime
                    ) {
                        Text(if (editingSchedule != null) "Update" else "Schedule")
                    }
                }
            }
        }
    }

    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "Ok")
            negativeButton(text = "Cancel")
        }
    ) {
        datepicker(
            initialDate = selectedDate,
            title = "Pick a date",
            allowedDateValidator = { date ->
                // Only allow today and future dates
                !date.isBefore(LocalDate.now())
            }
        ) { date ->
            selectedDate = date
        }
    }

    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton(text = "Ok")
            negativeButton(text = "Cancel")
        }
    ) {
        timepicker(
            initialTime = selectedTime,
            title = "Pick a time"
        ) { time ->
            selectedTime = time
        }
    }
}