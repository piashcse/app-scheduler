package com.piashcse.appscheduler.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.piashcse.appscheduler.data.local.AppSchedulerDatabase
import com.piashcse.appscheduler.data.model.ScheduleStatus
import com.piashcse.appscheduler.utils.AlarmUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val database = AppSchedulerDatabase.getDatabase(context)
            val scheduleDao = database.scheduleDao()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Get pending schedules directly using suspend function
                    val pendingSchedules = scheduleDao.getSchedulesByStatusSync(ScheduleStatus.PENDING)

                    pendingSchedules.forEach { schedule ->
                        if (schedule.scheduledTime > System.currentTimeMillis()) {
                            AlarmUtils.scheduleAlarm(
                                context,
                                schedule.id,
                                schedule.packageName,
                                schedule.appName,
                                schedule.scheduledTime
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Log error or handle gracefully
                }
            }
        }
    }
}