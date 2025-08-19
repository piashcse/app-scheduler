package com.piashcse.appscheduler.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.piashcse.appscheduler.data.local.entity.ScheduleEntity

@Composable
fun SchedulesList(
    schedules: List<ScheduleEntity>,
    onEditSchedule: (ScheduleEntity) -> Unit,
    onCancelSchedule: (ScheduleEntity) -> Unit,
    onDeleteSchedule: (ScheduleEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (schedules.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No schedules found\nTap + to create your first schedule",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(schedules) { schedule ->
                ScheduleItem(
                    schedule = schedule,
                    onEdit = { onEditSchedule(schedule) },
                    onCancel = { onCancelSchedule(schedule) },
                    onDelete = { onDeleteSchedule(schedule) }
                )
            }
        }
    }
}