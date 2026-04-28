package com.kov.module_5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.room.Room
import com.kov.module_5.data.local.AppDatabase
import com.kov.module_5.data.repository.TaskRepositoryImpl
import com.kov.module_5.data.repository.preferences.SettingsRepositoryImpl
import com.kov.module_5.domain.usecase.*
import com.kov.module_5.presentation.ui.screen.TaskListScreen
import com.kov.module_5.presentation.viewmodel.TaskViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "todo_database"
        ).build()

        val taskRepository = TaskRepositoryImpl(database.taskDao(), applicationContext)
        val settingsRepository = SettingsRepositoryImpl(applicationContext)

        val factory = TaskViewModelFactory(
            GetTasksUseCase(taskRepository),
            AddTaskUseCase(taskRepository),
            UpdateTaskUseCase(taskRepository),
            DeleteTaskUseCase(taskRepository),
            ImportTasksUseCase(taskRepository),
            settingsRepository
        )

        setContent {
            val viewModel: com.kov.module_5.presentation.viewmodel.TaskViewModel = viewModel(factory = factory)
            TaskListScreen(viewModel)
        }
    }
}
