package com.example.kotin4.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.kotin4.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerService : Service() {

    companion object {
        const val CHANNEL_ID = "timer_channel"
        const val NOTIFICATION_ID = 1001

        // Сколько секунд прошло
        private val _seconds = MutableStateFlow(0)
        val seconds: StateFlow<Int> = _seconds.asStateFlow()

        // Запущен ли сервис
        private val _running = MutableStateFlow(false)
        val running: StateFlow<Boolean> = _running.asStateFlow()

        // Действия для интента (опционально)
        const val ACTION_START = "com.example.kotin4.action.START"
        const val ACTION_STOP = "com.example.kotin4.action.STOP"
    }

    // Scope сервиса для корутин
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopSelf()
            }
            ACTION_START, null -> {
                startTimerForeground()
            }
            else -> {
                startTimerForeground()
            }
        }
        return START_NOT_STICKY
    }

    // Запускаем foreground и тик каждую секунду
    private fun startTimerForeground() {
        if (_running.value) return
        _running.value = true

        // Запускаем foreground уведомление
        startForeground(NOTIFICATION_ID, buildNotification(_seconds.value))

        timerJob?.cancel()
        timerJob = serviceScope.launch {
            try {
                while (isActive) {
                    delay(1000L)
                    _seconds.value = _seconds.value + 1
                    updateNotification(_seconds.value)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Если ошибка — помечаем как остановленный
                _running.value = false
            }
        }
    }

    // Обновляем уведомление
    private fun updateNotification(sec: Int) {
        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_ID, buildNotification(sec))
    }

    // Строим уведомление с текстом "Прошло X секунд"
    private fun buildNotification(sec: Int): Notification {
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Если нет разрешения, возвращаем базовое уведомление
        }

        val tapIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, tapIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val title = "Счётчик времени"
        val text = "Прошло $sec секунд"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        timerJob?.cancel()
        _running.value = false
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // Создаём канал уведомлений
    private fun createNotificationChannel() {
        val name = "Сервис счётчика"
        val descriptionText = "Уведомление с таймером в foreground"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }
}
