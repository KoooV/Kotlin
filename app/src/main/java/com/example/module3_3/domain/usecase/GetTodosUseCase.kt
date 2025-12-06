package com.example.module3_3.domain.usecase

import com.example.module3_3.domain.model.TodoItem
import com.example.module3_3.domain.repository.TodoRepository

class GetTodosUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(): List<TodoItem> = repository.getTodos()
}

