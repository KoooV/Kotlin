package com.example.kotlin48_414

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context)
        // Перезаписываем будильник на следующие 20:00 (завтра)
        AlarmScheduler.schedule(context)
    }

    private fun showNotification(context: Context) {
        // Проверяем разрешение на Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) return
        }

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Напоминание о таблетке",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Ежедневное напоминание принять таблетку"
            enableVibration(true)
        }
        nm.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("💊 Напоминание")
            .setContentText("Время принять таблетку!")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Время принять таблетку!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        nm.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "pill_reminder_channel"
        const val NOTIFICATION_ID = 1001
    }
}

