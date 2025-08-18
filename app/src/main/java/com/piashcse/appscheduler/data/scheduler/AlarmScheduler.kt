package com.piashcse.appscheduler.data.scheduler

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import com.piashcse.appscheduler.data.local.AppDatabase
import com.piashcse.appscheduler.data.local.entity.Schedule

class AlarmScheduler(private val context: Context, private val db: AppDatabase) {
    private val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun makePendingIntent(scheduleId: Long): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_SCHEDULE_ID, scheduleId)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, scheduleId.toInt(), intent, flags)
    }

    suspend fun schedule(schedule: Schedule) {
        // check time conflicts: disallow other schedule at same timestamp
        val existing = db.scheduleDao().getByExactTime(schedule.scheduledTimeMillis)
        if (existing.isNotEmpty()) {
            throw IllegalArgumentException("Conflict: another schedule exists at the exact same time.")
        }

        val id = db.scheduleDao().insert(schedule.copy(createdAt = System.currentTimeMillis()))
        val pending = makePendingIntent(id)
        val triggerAt = schedule.scheduledTimeMillis

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+: may require SCHEDULE_EXACT_ALARM permission for exact alarms
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        }
    }

    suspend fun cancel(scheduleId: Long) {
        val pending = makePendingIntent(scheduleId)
        am.cancel(pending)

        val schedule = db.scheduleDao().getById(scheduleId) ?: return
        db.scheduleDao().update(schedule.copy(enabled = false, updatedAt = System.currentTimeMillis()))
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    suspend fun update(scheduleId: Long, newTimeMillis: Long) {
        val schedule = db.scheduleDao().getById(scheduleId) ?: throw IllegalArgumentException("Not found")
        // check for conflicts
        val existing = db.scheduleDao().getByExactTime(newTimeMillis).filter { it.id != scheduleId }
        if (existing.isNotEmpty()) {
            throw IllegalArgumentException("Conflict: another schedule exists at the exact same time.")
        }

        // cancel previous alarm
        val oldPending = makePendingIntent(scheduleId)
        am.cancel(oldPending)

        // update DB
        val updated = schedule.copy(scheduledTimeMillis = newTimeMillis, updatedAt = System.currentTimeMillis())
        db.scheduleDao().update(updated)

        // register new alarm
        val newPending = makePendingIntent(scheduleId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, newTimeMillis, newPending)
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, newTimeMillis, newPending)
        }
    }
}