package com.piashcse.appscheduler.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.piashcse.appscheduler.data.local.entity.ScheduleEntity
import com.piashcse.appscheduler.data.model.AppInfo
import com.piashcse.appscheduler.utils.TimeUtils
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDialog(
    selectedApp: AppInfo?,
    editingSchedule: ScheduleEntity?,
    onSchedule: (AppInfo, Long) -> Unit,
    onDismiss: () -> Unit
) {
    val isEditing = editingSchedule != null
    val appInfo = selectedApp ?: run {
        if (isEditing) {
            AppInfo(
                packageName = editingSchedule.packageName,
                appName = editingSchedule.appName
            )
        } else {
            onDismiss()
            return
        }
    }

    // Initialize with current schedule time or current time + 1 minute
    val initialTime = if (isEditing) {
        Date(editingSchedule!!.scheduledTime)
    } else {
        Calendar.getInstance().apply {
            add(Calendar.MINUTE, 1)
        }.time
    }

    var selectedDate by remember {
        mutableStateOf(
            initialTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        )
    }

    var selectedTime by remember {
        mutableStateOf(
            initialTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
        )
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isEditing) "Edit Schedule" else "Schedule App",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "App: ${appInfo.appName}",
                    style = MaterialTheme.typography.bodyMedium
                )


                // Date selection
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { dateDialogState.show() }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Date",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = TimeUtils.formatDate(
                                selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            ),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Time selection
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { timeDialogState.show() }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Time",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text =  TimeUtils.formatTime12Hour(
                                selectedTime.atDate(selectedDate)
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli()
                            ),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Preview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Scheduled for:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = TimeUtils.formatDateTime12Hour(
                                selectedDate.atTime(selectedTime)
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli()
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val timestamp = selectedDate.atTime(selectedTime)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()
                            onSchedule(appInfo, timestamp)
                        }
                    ) {
                        Text(if (isEditing) "Update" else "Schedule")
                    }
                }
            }
        }
    }

    // Date picker dialog
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "Ok")
            negativeButton(text = "Cancel")
        }
    ) {
        datepicker(
            initialDate = selectedDate,
            title = "Pick a date"
        ) {
            selectedDate = it
        }
    }

    // Time picker dialog with 12/24 hour format
    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton(text = "Ok")
            negativeButton(text = "Cancel")
        }
    ) {
        timepicker(
            initialTime = selectedTime,
            title = "Pick a time",
            is24HourClock = false
        ) {
            selectedTime = it
        }
    }
}