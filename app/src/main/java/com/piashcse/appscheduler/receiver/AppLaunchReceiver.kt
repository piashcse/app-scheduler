package com.piashcse.appscheduler.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.piashcse.appscheduler.data.local.AppSchedulerDatabase
import com.piashcse.appscheduler.data.model.ScheduleStatus
import com.piashcse.appscheduler.utils.AppUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppLaunchReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getLongExtra("schedule_id", -1)
        val packageName = intent.getStringExtra("package_name") ?: return
        val appName = intent.getStringExtra("app_name") ?: return

        if (scheduleId == -1L) return

        val database = AppSchedulerDatabase.getDatabase(context)
        val scheduleDao = database.scheduleDao()

        CoroutineScope(Dispatchers.IO).launch {
            val schedule = scheduleDao.getScheduleById(scheduleId)
            if (schedule != null && schedule.status == ScheduleStatus.PENDING) {
                val success = AppUtils.launchApp(context, packageName)

                val updatedSchedule = schedule.copy(
                    status = if (success) ScheduleStatus.EXECUTED else ScheduleStatus.FAILED,
                    executedAt = System.currentTimeMillis()
                )

                scheduleDao.updateSchedule(updatedSchedule)
            }
        }
    }
}