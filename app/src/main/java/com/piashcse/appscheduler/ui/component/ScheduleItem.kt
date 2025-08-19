package com.piashcse.appscheduler.ui.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.piashcse.appscheduler.data.local.entity.ScheduleEntity
import com.piashcse.appscheduler.data.model.ScheduleStatus
import com.piashcse.appscheduler.utils.TimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListItem(
    schedule: ScheduleEntity,
    onEdit: (ScheduleEntity) -> Unit,
    onCancel: (ScheduleEntity) -> Unit,
    onDelete: (ScheduleEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (schedule.status) {
        ScheduleStatus.PENDING -> MaterialTheme.colorScheme.primary
        ScheduleStatus.EXECUTED -> Color(0xFF4CAF50)
        ScheduleStatus.CANCELLED -> Color(0xFF9E9E9E)
        ScheduleStatus.FAILED -> MaterialTheme.colorScheme.error
    }

    val isOverdue = schedule.status == ScheduleStatus.PENDING &&
            TimeUtils.isTimeInPast(schedule.scheduledTime)

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with app name and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = schedule.appName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = statusColor,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = schedule.status.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scheduled time with 12-hour format
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Scheduled: ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = TimeUtils.formatDateTime12Hour(schedule.scheduledTime),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }

            // Time remaining or execution info
            if (schedule.status == ScheduleStatus.PENDING) {
                if (isOverdue) {
                    Text(
                        text = "⚠️ Overdue",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "⏰ ${TimeUtils.getTimeRemaining(schedule.scheduledTime)} remaining",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (schedule.executedAt != null) {
                Text(
                    text = "Executed: ${TimeUtils.formatDateTime12Hour(schedule.executedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Package name
            Text(
                text = schedule.packageName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Action buttons
            if (schedule.status == ScheduleStatus.PENDING || schedule.status == ScheduleStatus.FAILED) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Edit button
                    IconButton(onClick = { onEdit(schedule) }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Schedule",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Delete button
                    IconButton(onClick = { onDelete(schedule) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Schedule",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
