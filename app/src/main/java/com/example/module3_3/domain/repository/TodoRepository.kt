package com.example.module3_3.domain.repository

import com.example.module3_3.domain.model.TodoItem

interface TodoRepository {
    suspend fun getTodos(): List<TodoItem>
    suspend fun toggleTodo(id: Int)
}

