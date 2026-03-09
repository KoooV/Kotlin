package com.example.kotin4.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class RandomNumberService : Service() {

    // Binder, который возвращаем клиенту при bindService
    inner class RandomBinder : Binder() {
        fun getService(): RandomNumberService = this@RandomNumberService
    }

    private val binder = RandomBinder()

    // Scope сервиса для корутин
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var generatorJob: Job? = null

    // Последнее сгенерированное число
    private val _number = MutableStateFlow<Int?>(null)
    val number: StateFlow<Int?> = _number.asStateFlow()

    override fun onBind(intent: Intent?): IBinder {
        // Клиент подключился — запускаем генерацию
        startGenerating()
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        // Клиент отключился — останавливаем генерацию
        stopGenerating()
        return false
    }

    override fun onDestroy() {
        stopGenerating()
        serviceScope.cancel()
        super.onDestroy()
    }

    // Запускаем генерацию случайных чисел каждую секунду
    private fun startGenerating() {
        if (generatorJob?.isActive == true) return
        generatorJob = serviceScope.launch {
            while (isActive) {
                _number.value = Random.nextInt(0, 101)
                delay(1000L)
            }
        }
    }

    // Останавливаем генерацию
    private fun stopGenerating() {
        generatorJob?.cancel()
        generatorJob = null
        _number.value = null
    }
}

