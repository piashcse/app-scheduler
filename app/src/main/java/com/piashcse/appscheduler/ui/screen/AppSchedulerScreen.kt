package com.piashcse.appscheduler.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.piashcse.appscheduler.ui.component.ScheduleDialog
import com.piashcse.appscheduler.ui.component.TabContent
import com.piashcse.appscheduler.ui.viewmodel.MainViewModel

@RequiresApi(Build.VERSION_CODES.O)
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
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