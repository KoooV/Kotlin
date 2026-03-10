package com.example.kotlin48_414

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class CurrencyViewModel : ViewModel() {

    private val _rate = MutableStateFlow(generateRate())
    val rate: StateFlow<Double> = _rate.asStateFlow()

    private val _previousRate = MutableStateFlow(_rate.value)
    val previousRate: StateFlow<Double> = _previousRate.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                delay(5_000L)
                emitNewRate()
            }
        }
    }

    fun refreshNow() {
        viewModelScope.launch {
            _isRefreshing.value = true
            delay(300L) // небольшая имитация задержки
            emitNewRate()
            _isRefreshing.value = false
        }
    }

    private fun emitNewRate() {
        _previousRate.value = _rate.value
        _rate.value = generateRate()
    }

    private fun generateRate(): Double {
        // 90.5 ± 2.0 → диапазон 88.5..92.5
        val value = 88.5 + Random.nextDouble() * 4.0
        return Math.round(value * 100.0) / 100.0
    }
}

