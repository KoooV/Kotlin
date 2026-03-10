package com.example.kotlin48_414

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences(
        BootReceiver.PREFS_NAME, Context.MODE_PRIVATE
    )

    private val _enabled = MutableStateFlow(prefs.getBoolean(BootReceiver.KEY_ENABLED, false))
    val enabled: StateFlow<Boolean> = _enabled

    // Время следующего срабатывания в миллисекундах (0 если выключено)
    private val _nextAlarmMs = MutableStateFlow(
        if (_enabled.value) AlarmScheduler.nextAlarmTimeMillis() else 0L
    )
    val nextAlarmMs: StateFlow<Long> = _nextAlarmMs

    fun enable() {
        val context = getApplication<Application>()
        AlarmScheduler.schedule(context)
        prefs.edit { putBoolean(BootReceiver.KEY_ENABLED, true) }
        _enabled.value = true
        _nextAlarmMs.value = AlarmScheduler.nextAlarmTimeMillis()
    }

    fun disable() {
        val context = getApplication<Application>()
        AlarmScheduler.cancel(context)
        prefs.edit { putBoolean(BootReceiver.KEY_ENABLED, false) }
        _enabled.value = false
        _nextAlarmMs.value = 0L
    }
}
