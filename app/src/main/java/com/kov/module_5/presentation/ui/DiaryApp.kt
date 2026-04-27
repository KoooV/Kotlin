package com.kov.module_5.presentation.ui

import androidx.compose.runtime.*
import com.kov.module_5.domain.model.DiaryEntry
import com.kov.module_5.presentation.viewmodel.DiaryViewModel

sealed class Screen {
    object Main : Screen()
    data class Edit(val entry: DiaryEntry?) : Screen()
}

@Composable
fun DiaryApp(viewModel: DiaryViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }

    when (val screen = currentScreen) {
        is Screen.Main -> {
            MainScreen(
                entries = viewModel.entries,
                onAddClick = { currentScreen = Screen.Edit(null) },
                onEntryClick = { entry -> currentScreen = Screen.Edit(entry) },
                onDeleteClick = { fileName -> viewModel.deleteEntry(fileName) }
            )
        }
        is Screen.Edit -> {
            EditScreen(
                entry = screen.entry,
                onSave = { title, content ->
                    viewModel.saveEntry(screen.entry?.fileName, title, content)
                    currentScreen = Screen.Main
                },
                onBack = { currentScreen = Screen.Main }
            )
        }
    }
}

