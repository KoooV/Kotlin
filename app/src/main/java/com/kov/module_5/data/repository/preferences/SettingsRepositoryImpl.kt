package com.kov.module_5.data.repository.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.kov.module_5.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    private val COMPLETED_COLOR_KEY = booleanPreferencesKey("completed_color")

    override val completedColorPreference: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[COMPLETED_COLOR_KEY] ?: false
    }

    override suspend fun setCompletedColorPreference(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[COMPLETED_COLOR_KEY] = enabled
        }
    }
}

