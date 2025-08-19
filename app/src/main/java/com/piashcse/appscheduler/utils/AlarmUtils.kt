package com.piashcse.appscheduler.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.piashcse.appscheduler.receiver.AppLaunchReceiver

object AlarmUtils {

    private const val TAG = "AlarmUtils"

    /**
     * Schedule an alarm to launch an app at the specified time
     */
    fun scheduleAlarm(
        context: Context,
        scheduleId: Long,
        packageName: String,
        appName: String,
        scheduledTime: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AppLaunchReceiver::class.java).apply {
            putExtra("schedule_id", scheduleId)
            putExtra("package_name", packageName)
            putExtra("app_name", appName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            scheduleId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    scheduledTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    scheduledTime,
                    pendingIntent
                )
            }

            Log.d(TAG, "Alarm scheduled for $appName at ${TimeUtils.formatDateTime12Hour(scheduledTime)}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm for $appName", e)
            throw e
        }
    }

    /**
     * Cancel an existing alarm
     */
    fun cancelAlarm(context: Context, scheduleId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AppLaunchReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            scheduleId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

        Log.d(TAG, "Alarm cancelled for schedule ID: $scheduleId")
    }

    /**
     * Check if exact alarms are allowed (Android 12+)
     */
    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
}