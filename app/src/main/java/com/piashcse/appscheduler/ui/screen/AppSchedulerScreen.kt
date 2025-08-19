package com.piashcse.appscheduler.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.piashcse.appscheduler.ui.component.ScheduleDialog
import com.piashcse.appscheduler.ui.component.TabContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSchedulerScreen(
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showScheduleDialog by viewModel.showScheduleDialog
    val selectedApp by viewModel.selectedApp
    val editingSchedule by viewModel.editingSchedule

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Schedules", "Apps")
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar for messages
    LaunchedEffect(uiState.showMessage) {
        if (uiState.showMessage) {
            snackbarHostState.showSnackbar(
                message = uiState.message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Scheduler") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedTabIndex = 1 // Switch to Apps tab
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }

            TabContent(
                selectedTabIndex = selectedTabIndex,
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    // Schedule Dialog
    if (showScheduleDialog) {
        ScheduleDialog(
            selectedApp = selectedApp,
            editingSchedule = editingSchedule,
            onSchedule = { app, time ->
                if (editingSchedule != null) {
                    viewModel.updateSchedule(editingSchedule!!, time)
                } else {
                    viewModel.scheduleApp(app, time)
                }
            },
            onDismiss = { viewModel.hideScheduleDialog() }
        )
    }
}