package com.kov.module_5.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kov.module_5.domain.model.DiaryEntry
import com.kov.module_5.domain.usecase.DeleteEntryUseCase
import com.kov.module_5.domain.usecase.GetEntriesUseCase
import com.kov.module_5.domain.usecase.SaveEntryUseCase
import kotlinx.coroutines.launch

class DiaryViewModel(
    private val getEntriesUseCase: GetEntriesUseCase,
    private val saveEntryUseCase: SaveEntryUseCase,
    private val deleteEntryUseCase: DeleteEntryUseCase
) : ViewModel() {

    private val _entries = mutableStateListOf<DiaryEntry>()
    val entries: List<DiaryEntry> = _entries

    init {
        loadEntries()
    }

    private fun loadEntries() {
        viewModelScope.launch {
            val loaded = getEntriesUseCase()
            _entries.clear()
            _entries.addAll(loaded)
        }
    }

    fun saveEntry(originalFileName: String?, title: String, content: String) {
        viewModelScope.launch {
            val newEntry = saveEntryUseCase(originalFileName, title, content)
            
            if (originalFileName != null) {
                _entries.removeAll { it.fileName == originalFileName }
            }
            _entries.add(0, newEntry)
            _entries.sortByDescending { it.timestamp }
        }
    }

    fun deleteEntry(fileName: String) {
        viewModelScope.launch {
            deleteEntryUseCase(fileName)
            _entries.removeAll { it.fileName == fileName }
        }
    }
}

