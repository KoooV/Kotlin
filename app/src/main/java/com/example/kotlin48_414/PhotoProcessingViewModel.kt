package com.example.kotlin48_414

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.kotlin48_414.workers.WeatherReportWorker
import com.example.kotlin48_414.workers.WeatherWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class WeatherStep { IDLE, LOADING, REPORT, DONE, ERROR }

data class CityWeatherState(
    val city: String,
    val temperature: Int? = null,
    val done: Boolean = false
)

data class WeatherForecastState(
    val step: WeatherStep = WeatherStep.IDLE,
    val cities: List<CityWeatherState> = emptyList(),
    val report: String? = null,
    val errorMessage: String? = null
)

class PhotoProcessingViewModel(application: Application) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)
    private val notificationManager =
        application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val _state = MutableStateFlow(WeatherForecastState())
    val state: StateFlow<WeatherForecastState> = _state

    private val cityList = listOf("Москва", "Лондон", "Нью-Йорк", "Токио")

    private fun ensureChannel() {
        val channel = NotificationChannel(
            WeatherWorker.CHANNEL_ID,
            "Прогноз погоды",
            NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Уведомления о загрузке прогноза погоды" }
        notificationManager.createNotificationChannel(channel)
    }

    private fun showProgressNotification(text: String) {
        ensureChannel()
        val notification = NotificationCompat.Builder(getApplication(), WeatherWorker.CHANNEL_ID)
            .setContentTitle("Загрузка погоды")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setProgress(0, 0, true)
            .build()
        notificationManager.notify(PROGRESS_NOTIF_ID, notification)
    }

    private fun cancelProgressNotification() {
        notificationManager.cancel(PROGRESS_NOTIF_ID)
    }

    fun startForecast() {
        _state.value = WeatherForecastState(
            step = WeatherStep.LOADING,
            cities = cityList.map { CityWeatherState(it) }
        )

        showProgressNotification("Загружаем погоду для ${cityList.size} городов…")

        // Создаём параллельные запросы для каждого города
        val cityRequests = cityList.mapIndexed { index, city ->
            OneTimeWorkRequestBuilder<WeatherWorker>()
                .setInputData(
                    workDataOf(
                        WeatherWorker.KEY_CITY to city,
                        WeatherWorker.KEY_NOTIF_ID to (WeatherWorker.NOTIFICATION_ID + index)
                    )
                )
                .addTag("weather_$city")
                .build()
        }

        // Запрос финального отчёта — будет запущен после всех параллельных
        val reportRequest = OneTimeWorkRequestBuilder<WeatherReportWorker>()
            .setInputMerger(ArrayCreatingInputMerger::class)
            .addTag("weather_report")
            .build()

        // Запускаем параллельно, затем финальный
        workManager.beginWith(cityRequests)
            .then(reportRequest)
            .enqueue()

        // Наблюдаем за каждым городом
        cityRequests.forEachIndexed { index, request ->
            val city = cityList[index]
            viewModelScope.launch {
                workManager.getWorkInfoByIdFlow(request.id).collect { info ->
                    if (info == null) return@collect
                    when (info.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            val temp = info.outputData.getString(WeatherWorker.KEY_TEMPERATURE)?.toIntOrNull() ?: 0
                            val updatedCities = _state.value.cities.map {
                                if (it.city == city) it.copy(temperature = temp, done = true)
                                else it
                            }
                            _state.value = _state.value.copy(cities = updatedCities)

                            // Обновляем уведомление
                            val done = updatedCities.filter { it.done }.joinToString(", ") { it.city }
                            val pending = updatedCities.filter { !it.done }
                            if (pending.isEmpty()) {
                                showProgressNotification("Все данные получены, формируем отчёт…")
                            } else {
                                showProgressNotification("Готово: $done\n${pending.joinToString(", ") { it.city }} в процессе…")
                            }
                        }
                        WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                            cancelProgressNotification()
                            _state.value = _state.value.copy(
                                step = WeatherStep.ERROR,
                                errorMessage = "Ошибка получения погоды для $city"
                            )
                        }
                        else -> {}
                    }
                }
            }
        }

        // Наблюдаем за финальным отчётом
        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(reportRequest.id).collect { info ->
                if (info == null) return@collect
                when (info.state) {
                    WorkInfo.State.RUNNING -> {
                        _state.value = _state.value.copy(step = WeatherStep.REPORT)
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        val report = info.outputData.getString(WeatherReportWorker.KEY_REPORT)
                        cancelProgressNotification()
                        _state.value = _state.value.copy(
                            step = WeatherStep.DONE,
                            report = report
                        )
                    }
                    WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                        cancelProgressNotification()
                        _state.value = _state.value.copy(
                            step = WeatherStep.ERROR,
                            errorMessage = "Ошибка формирования отчёта"
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    fun reset() {
        workManager.cancelAllWork()
        cancelProgressNotification()
        _state.value = WeatherForecastState()
    }

    companion object {
        private const val PROGRESS_NOTIF_ID = 999
    }
}
