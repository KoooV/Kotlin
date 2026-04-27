package com.kov.module_5.domain.repository

import com.kov.module_5.domain.model.DiaryEntry

interface DiaryRepository {
    suspend fun getEntries(): List<DiaryEntry>
    suspend fun saveEntry(originalFileName: String?, title: String, content: String): DiaryEntry
    suspend fun deleteEntry(fileName: String)
}

