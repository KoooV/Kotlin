package com.kov.module_5.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kov.module_5.domain.model.Task
import com.kov.module_5.domain.repository.SettingsRepository
import com.kov.module_5.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class TaskViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val importTasksUseCase: ImportTasksUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _completedColorEnabled = MutableStateFlow(false)
    val completedColorEnabled: StateFlow<Boolean> = _completedColorEnabled

    init {
        viewModelScope.launch {
            getTasksUseCase().collectLatest { _tasks.value = it }
        }
        viewModelScope.launch {
            settingsRepository.completedColorPreference.collectLatest {
                _completedColorEnabled.value = it
            }
        }
    }

    fun addTask(title: String, description: String) {
        viewModelScope.launch {
            addTaskUseCase(Task(id = UUID.randomUUID().toString(), title = title, description = description, isCompleted = false))
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            deleteTaskUseCase(task)
        }
    }

    fun importTasks() {
        viewModelScope.launch {
            importTasksUseCase()
        }
    }

    fun toggleCompletedColor() {
        viewModelScope.launch {
            settingsRepository.setCompletedColorPreference(!_completedColorEnabled.value)
        }
    }
}

class TaskViewModelFactory(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val importTasksUseCase: ImportTasksUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskViewModel(
            getTasksUseCase,
            addTaskUseCase,
            updateTaskUseCase,
            deleteTaskUseCase,
            importTasksUseCase,
            settingsRepository
        ) as T
    }
}

