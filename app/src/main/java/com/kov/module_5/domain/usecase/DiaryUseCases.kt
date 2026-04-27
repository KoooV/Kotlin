package com.kov.module_5.domain.usecase

import com.kov.module_5.domain.model.DiaryEntry
import com.kov.module_5.domain.repository.DiaryRepository

class GetEntriesUseCase(private val repository: DiaryRepository) {
    suspend operator fun invoke(): List<DiaryEntry> {
        return repository.getEntries()
    }
}

class SaveEntryUseCase(private val repository: DiaryRepository) {
    suspend operator fun invoke(originalFileName: String?, title: String, content: String): DiaryEntry {
        return repository.saveEntry(originalFileName, title, content)
    }
}

class DeleteEntryUseCase(private val repository: DiaryRepository) {
    suspend operator fun invoke(fileName: String) {
        repository.deleteEntry(fileName)
    }
}

