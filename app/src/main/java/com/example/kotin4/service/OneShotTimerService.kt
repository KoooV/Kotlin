package com.example.kotin4.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.kotin4.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OneShotTimerService : Service() {

    companion object {
        const val EXTRA_SECONDS = "extra_seconds"

        // Канал для foreground-уведомления (отсчёт)
        const val CHANNEL_RUNNING = "timer_running_channel"

        // Канал для финального уведомления «Таймер завершён»
        const val CHANNEL_DONE = "timer_done_channel"

        const val NOTIFICATION_FG_ID = 43
        const val NOTIFICATION_DONE_ID = 42

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
        createChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val totalSeconds = intent?.getIntExtra(EXTRA_SECONDS, 0) ?: 0
        if (totalSeconds <= 0) {
            stopSelf()
            return START_NOT_STICKY
        }

        // Сразу поднимаем foreground, иначе Android убьёт сервис за 5 сек
        _remaining.value = totalSeconds
        _running.value = true
        startForeground(NOTIFICATION_FG_ID, buildRunningNotification(totalSeconds))

        // Отменяем предыдущий таймер, если был
        timerJob?.cancel()

        timerJob = serviceScope.launch {
            // Обратный отсчёт каждую секунду
            for (i in totalSeconds downTo 1) {
                _remaining.value = i
                updateForegroundNotification(i)
                delay(1000L)
            }

            _remaining.value = 0
            _running.value = false

            // Показываем финальное уведомление «Таймер завершён!»
            showFinishedNotification()

            // Снимаем foreground и останавливаем сервис
            @Suppress("DEPRECATION")
            stopForeground(true)
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

    // Foreground-уведомление: «Идёт отсчёт: X сек»
    private fun buildRunningNotification(sec: Int): Notification {
        val tapIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, tapIntent, PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_RUNNING)
            .setContentTitle("Таймер запущен")
            .setContentText("Осталось: $sec сек.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    // Обновляем foreground-уведомление каждую секунду
    private fun updateForegroundNotification(sec: Int) {
        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_FG_ID, buildRunningNotification(sec))
    }

    // Финальное уведомление «Таймер завершён!»
    private fun showFinishedNotification() {
        val tapIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 1, tapIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_DONE)
            .setContentTitle("Таймер завершён!")
            .setContentText("Время вышло")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_DONE_ID, notification)
    }

    // Создание каналов уведомлений
    private fun createChannels() {
        val manager = getSystemService(NotificationManager::class.java) ?: return

        val runningChannel = NotificationChannel(
            CHANNEL_RUNNING,
            "Таймер в процессе",
            NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Отображается пока таймер тикает" }

        val doneChannel = NotificationChannel(
            CHANNEL_DONE,
            "Таймер завершён",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Уведомление о завершении таймера" }

        manager.createNotificationChannel(runningChannel)
        manager.createNotificationChannel(doneChannel)
    }
}
