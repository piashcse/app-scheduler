package com.piashcse.appscheduler.data.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.piashcse.appscheduler.data.local.DatabaseProvider
import com.piashcse.appscheduler.data.local.entity.ExecutionLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getLongExtra(EXTRA_SCHEDULE_ID, -1L)
        if (scheduleId == -1L) return

        // Ideally run short work. Use CoroutineScope + DB & Notification.
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch @androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS) {
            val db = DatabaseProvider.get(appContext) // your singleton
            val schedule = db.scheduleDao().getById(scheduleId)
            if (schedule == null) {
                db.executionLogDao().insert(
                    ExecutionLog(
                        scheduleId = scheduleId,
                        status = "SCHEDULE_NOT_FOUND",
                        details = null
                    )
                )
                return@launch
            }

            if (!schedule.enabled) {
                db.executionLogDao().insert(
                    ExecutionLog(scheduleId = scheduleId, status = "DISABLED", details = null)
                )
                return@launch
            }

            val pm = appContext.packageManager
            val launchIntent = pm.getLaunchIntentForPackage(schedule.packageName)

            if (launchIntent == null) {
                db.executionLogDao().insert(
                    ExecutionLog(scheduleId = scheduleId, status = "APP_NOT_INSTALLED", details = schedule.packageName)
                )
                return@launch
            }

            // Try to start the app. Because we are in a BroadcastReceiver (background), startActivity may be blocked.
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                appContext.startActivity(launchIntent)
                db.executionLogDao().insert(
                    ExecutionLog(scheduleId = scheduleId, status = "SUCCESS", details = "Launched directly")
                )
            } catch (e: Exception) {
                // Most likely background start blocked. Send notification as fallback.
                val notifId = (scheduleId % Int.MAX_VALUE).toInt()
                NotificationHelper.showOpenAppNotification(appContext, schedule.packageName, notifId, scheduleId)
                db.executionLogDao().insert(
                    ExecutionLog(scheduleId = scheduleId, status = "NOTIFICATION_SENT", details = e.message)
                )
            }
        }
    }

    companion object {
        const val EXTRA_SCHEDULE_ID = "extra_schedule_id"
    }
}