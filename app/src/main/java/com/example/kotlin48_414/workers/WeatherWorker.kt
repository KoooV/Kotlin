package com.example.kotlin48_414.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import kotlin.random.Random

class WeatherWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val city = inputData.getString(KEY_CITY) ?: return Result.failure()

        // Имитация загрузки погоды (1–3 секунды)
        val delayMs = Random.nextLong(1000, 3000)
        delay(delayMs)

        if (isStopped) return Result.failure()

        // Генерируем случайную температуру
        val temperature = Random.nextInt(-10, 35)

        return Result.success(
            workDataOf(
                KEY_CITY to city,
                KEY_TEMPERATURE to temperature.toString()
            )
        )
    }

    companion object {
        const val KEY_CITY = "city"
        const val KEY_TEMPERATURE = "temperature"
        const val KEY_NOTIF_ID = "notif_id"
        const val NOTIFICATION_ID = 100
        const val CHANNEL_ID = "weather_channel"
    }
}
