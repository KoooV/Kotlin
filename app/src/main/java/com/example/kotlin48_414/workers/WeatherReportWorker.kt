package com.example.kotlin48_414.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

class WeatherReportWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        // ArrayCreatingInputMerger собирает значения одного ключа в String-массив
        // WeatherWorker передаёт city и temperature как строки
        val cities = inputData.getStringArray(KEY_CITY_MERGED) ?: emptyArray()
        val tempsRaw = inputData.getStringArray(KEY_TEMP_MERGED) ?: emptyArray()
        val temps = tempsRaw.mapNotNull { it.toIntOrNull() }

        setForeground(createForegroundInfo("Формируем отчёт…"))
        delay(1000)

        if (isStopped) return Result.failure()

        val avgTemp = if (temps.isNotEmpty()) temps.sum() / temps.size else 0
        val sign = if (avgTemp >= 0) "+" else ""
        val summary = "Средняя температура $sign${avgTemp}°C"

        updateFinalNotification("Отчёт готов! $summary")

        return Result.success(
            workDataOf(
                KEY_REPORT to summary,
                KEY_AVG_TEMP to avgTemp
            )
        )
    }

    private fun createForegroundInfo(text: String): ForegroundInfo {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Прогноз погоды")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setProgress(100, 90, false)
            .build()
        return ForegroundInfo(
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }

    private fun updateFinalNotification(text: String) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Прогноз погоды")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(false)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Прогноз погоды",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Уведомления о загрузке прогноза погоды"
        }
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        // Ключи для получения объединённых данных от WeatherWorker-ов
        const val KEY_CITY_MERGED = "city"
        const val KEY_TEMP_MERGED = "temperature"
        const val KEY_REPORT = "report"
        const val KEY_AVG_TEMP = "avg_temp"
        const val NOTIFICATION_ID = 200
        const val CHANNEL_ID = "weather_channel"
    }
}
