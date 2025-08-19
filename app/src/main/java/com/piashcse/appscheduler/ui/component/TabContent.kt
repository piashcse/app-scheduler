package com.piashcse.appscheduler.ui.component

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.piashcse.appscheduler.ui.viewmodel.MainViewModel

@Composable
fun TabContent(
    selectedTabIndex: Int,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    when (selectedTabIndex) {
        0 -> {
            val schedules by viewModel.schedules.collectAsStateWithLifecycle()
            SchedulesList(
                schedules = schedules,
                onEditSchedule = { schedule ->
                    viewModel.showScheduleDialog(schedule = schedule)
                },
                onCancelSchedule = viewModel::cancelSchedule,
                onDeleteSchedule = viewModel::deleteSchedule,
                modifier = modifier
            )
        }
        1 -> {
            val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
            AppsList(
                apps = installedApps,
                onAppSelected = { app ->
                    viewModel.showScheduleDialog(appInfo = app)
                },
                modifier = modifier
            )
        }
    }
}
