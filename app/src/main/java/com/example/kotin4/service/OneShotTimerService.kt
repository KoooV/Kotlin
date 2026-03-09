package com.example.kotin4.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.kotin4.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OneShotTimerService : Service() {

    companion object {
        const val EXTRA_SECONDS = "extra_seconds"
        const val CHANNEL_ID = "timer_done_channel"
        const val NOTIFICATION_ID = 42

        // Оставшееся время — UI подписывается на это
        private val _remaining = MutableStateFlow(0)
        val remaining: StateFlow<Int> = _remaining.asStateFlow()

        // Запущен ли таймер
        private val _running = MutableStateFlow(false)
        val running: StateFlow<Boolean> = _running.asStateFlow()
    }

    // Scope для задач сервиса
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val totalSeconds = intent?.getIntExtra(EXTRA_SECONDS, 0) ?: 0
        if (totalSeconds <= 0) {
            stopSelf()
            return START_NOT_STICKY
        }

        // Отменяем предыдущий таймер, если был
        timerJob?.cancel()

        _remaining.value = totalSeconds
        _running.value = true

        timerJob = serviceScope.launch {
            // Обратный отсчёт каждую секунду
            for (i in totalSeconds downTo 1) {
                _remaining.value = i
                delay(1000L)
            }
            _remaining.value = 0

            // Таймер завершён — показываем уведомление
            showFinishedNotification()

            // Сервис сам себя останавливает
            _running.value = false
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timerJob?.cancel()
        _running.value = false
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // Создание канала уведомлений
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Таймер завершён",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Уведомление о завершении одноразового таймера"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    // Уведомление «Таймер завершён!»
    private fun showFinishedNotification() {
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val tapIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, tapIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Таймер завершён!")
            .setContentText("Время вышло")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }
}

