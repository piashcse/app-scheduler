package com.piashcse.appscheduler.data.scheduler

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    private const val CHANNEL_ID = "app_scheduler_channel"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "App Scheduler"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = "Notifications to open scheduled apps"
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showOpenAppNotification(context: Context, packageName: String, notifId: Int, scheduleId: Long) {
        ensureChannel(context)
        val pm = context.packageManager
        val pkgLabel = try {
            val ai = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(ai).toString()
        } catch (e: Exception) {
            packageName
        }

        val launchIntent = pm.getLaunchIntentForPackage(packageName)
        val openPendingIntent = PendingIntent.getActivity(
            context,
            (scheduleId % Int.MAX_VALUE).toInt(),
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Scheduled: $pkgLabel")
            .setContentText("Tap to open the app scheduled for now.")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(notifId, notification)
    }
}