package com.example.kotlin48_414

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val enabled = prefs.getBoolean(KEY_ENABLED, false)
            if (enabled) {
                AlarmScheduler.schedule(context)
            }
        }
    }

    companion object {
        const val PREFS_NAME = "pill_reminder_prefs"
        const val KEY_ENABLED = "alarm_enabled"
    }
}

