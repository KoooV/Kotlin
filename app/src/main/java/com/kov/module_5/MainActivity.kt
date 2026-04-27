package com.kov.module_5

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.kov.module_5.data.repository.DiaryRepositoryImpl
import com.kov.module_5.domain.usecase.DeleteEntryUseCase
import com.kov.module_5.domain.usecase.GetEntriesUseCase
import com.kov.module_5.domain.usecase.SaveEntryUseCase
import com.kov.module_5.presentation.ui.DiaryApp
import com.kov.module_5.presentation.viewmodel.DiaryViewModel
import com.kov.module_5.presentation.viewmodel.DiaryViewModelFactory
import com.kov.module_5.ui.theme.Module_5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = DiaryRepositoryImpl(filesDir)
        val factory = DiaryViewModelFactory(
            GetEntriesUseCase(repository),
            SaveEntryUseCase(repository),
            DeleteEntryUseCase(repository)
        )
        val viewModel = ViewModelProvider(this, factory)[DiaryViewModel::class.java]

        setContent {
            Module_5Theme {
                DiaryApp(viewModel)
            }
        }
    }
}