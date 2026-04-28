package com.kov.module_5.domain.usecase

import com.kov.module_5.domain.model.Task
import com.kov.module_5.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksUseCase(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<Task>> = repository.getAllTasks()
}

class AddTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.insertTask(task)
}

class UpdateTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.updateTask(task)
}

class DeleteTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.deleteTask(task)
}

class ImportTasksUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke() = repository.importTasksFromJson()
}

