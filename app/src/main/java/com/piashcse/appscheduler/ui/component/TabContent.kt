package com.piashcse.appscheduler.ui.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.piashcse.appscheduler.data.model.ScheduleStatus
import com.piashcse.appscheduler.ui.screen.MainViewModel

@Composable
fun TabContent(
    selectedTabIndex: Int,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    when (selectedTabIndex) {
        0 -> SchedulesTab(viewModel, modifier)
        1 -> AppsTab(viewModel, modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchedulesTab(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val schedules by viewModel.schedules.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf(ScheduleStatus.PENDING) }

    val filteredSchedules = schedules.filter { schedule ->
        when (selectedFilter) {
            ScheduleStatus.PENDING -> schedule.status == ScheduleStatus.PENDING
            ScheduleStatus.EXECUTED -> schedule.status == ScheduleStatus.EXECUTED
            ScheduleStatus.CANCELLED -> schedule.status == ScheduleStatus.CANCELLED
            ScheduleStatus.FAILED -> schedule.status == ScheduleStatus.FAILED
        }
    }

    Column(modifier = modifier) {
        // Filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScheduleStatus.entries.forEach { status ->
                FilterChip(
                    onClick = { selectedFilter = status },
                    label = {
                        Text(
                            text = "${status.name} (${schedules.count { it.status == status }})"
                        )
                    },
                    selected = selectedFilter == status
                )
            }
        }

        if (filteredSchedules.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (selectedFilter) {
                        ScheduleStatus.PENDING -> "No pending schedules.\nTap + to schedule an app!"
                        ScheduleStatus.EXECUTED -> "No executed schedules yet."
                        ScheduleStatus.CANCELLED -> "No cancelled schedules."
                        ScheduleStatus.FAILED -> "No failed schedules."
                    },
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = filteredSchedules,
                    key = { it.id }
                ) { schedule ->
                    ScheduleListItem(
                        schedule = schedule,
                        onEdit = { viewModel.showScheduleDialog(schedule = it) },
                        onCancel = { viewModel.cancelSchedule(it) },
                        onDelete = { viewModel.deleteSchedule(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppsTab(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()

    if (installedApps.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = installedApps,
                key = { it.packageName }
            ) { app ->
                AppListItem(
                    appInfo = app,
                    onSchedule = { viewModel.showScheduleDialog(it) }
                )
            }
        }
    }
}
