package com.kov.module_5.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kov.module_5.domain.usecase.DeleteEntryUseCase
import com.kov.module_5.domain.usecase.GetEntriesUseCase
import com.kov.module_5.domain.usecase.SaveEntryUseCase

class DiaryViewModelFactory(
    private val getEntriesUseCase: GetEntriesUseCase,
    private val saveEntryUseCase: SaveEntryUseCase,
    private val deleteEntryUseCase: DeleteEntryUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
            return DiaryViewModel(getEntriesUseCase, saveEntryUseCase, deleteEntryUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

