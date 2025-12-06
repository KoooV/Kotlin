package com.example.module3_3.data.repository

import android.content.Context
import com.example.module3_3.data.datasource.TodoJsonDataSource
import com.example.module3_3.domain.model.TodoItem
import com.example.module3_3.domain.repository.TodoRepository

class TodoRepositoryImpl(context: Context) : TodoRepository {
    private val dataSource = TodoJsonDataSource(context)
    private val todos = dataSource.getTodos().map { dto ->
        TodoItem(dto.id, dto.title, dto.description, dto.isCompleted)
    }.toMutableList()

    override suspend fun getTodos(): List<TodoItem> {
        return todos.toList()
    }

    override suspend fun toggleTodo(id: Int) {
        val index = todos.indexOfFirst { it.id == id }
        if (index >= 0) {
            val t = todos[index]
            todos[index] = t.copy(isCompleted = !t.isCompleted)
        }
    }
}

