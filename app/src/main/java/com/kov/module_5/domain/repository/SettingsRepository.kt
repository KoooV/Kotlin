package com.kov.module_5.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val completedColorPreference: Flow<Boolean>
    suspend fun setCompletedColorPreference(enabled: Boolean)
}

