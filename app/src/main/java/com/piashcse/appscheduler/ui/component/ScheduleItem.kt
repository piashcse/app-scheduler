package com.piashcse.appscheduler.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.piashcse.appscheduler.data.local.entity.ScheduleEntity
import com.piashcse.appscheduler.data.model.ScheduleStatus
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScheduleItem(
    schedule: ScheduleEntity,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = schedule.appName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Scheduled: ${dateFormat.format(Date(schedule.scheduledTime))}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Status: ${schedule.status.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = when (schedule.status) {
                    ScheduleStatus.PENDING -> MaterialTheme.colorScheme.primary
                    ScheduleStatus.EXECUTED -> MaterialTheme.colorScheme.tertiary
                    ScheduleStatus.CANCELLED -> MaterialTheme.colorScheme.error
                    ScheduleStatus.FAILED -> MaterialTheme.colorScheme.error
                }
            )

            if (schedule.executedAt != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Executed: ${dateFormat.format(Date(schedule.executedAt))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (schedule.status == ScheduleStatus.PENDING) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Edit")
                    }

                    OutlinedButton(
                        onClick = { showCancelDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                }

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Schedule") },
            text = { Text("Are you sure you want to delete this schedule?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    // Cancel Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Schedule") },
            text = { Text("Are you sure you want to cancel this schedule?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCancel()
                        showCancelDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}