package com.example.module3_2

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CounterViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _count = MutableStateFlow(savedStateHandle.get<Int>(STATE_KEY) ?: 0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() {
        val new = _count.value + 1
        _count.value = new
        savedStateHandle.set(STATE_KEY, new)
    }

    companion object {
        private const val STATE_KEY = "count"
    }
}

