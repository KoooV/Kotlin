package com.example.module3_3.presentation.todo

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.module3_3.data.repository.TodoRepositoryImpl
import com.example.module3_3.domain.model.TodoItem
import com.example.module3_3.domain.usecase.GetTodosUseCase
import com.example.module3_3.domain.usecase.ToggleTodoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoViewModel(context: Context) : ViewModel() {
    private val repository = TodoRepositoryImpl(context)
    private val getTodos = GetTodosUseCase(repository)
    private val toggleTodo = ToggleTodoUseCase(repository)

    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())
    val todos: StateFlow<List<TodoItem>> = _todos

    init {
        loadTodos()
    }

    private fun loadTodos() {
        viewModelScope.launch {
            _todos.value = getTodos()
        }
    }

    fun onToggleTodo(id: Int) {
        viewModelScope.launch {
            toggleTodo(id)
            _todos.value = getTodos()
        }
    }

    fun getTodoById(id: Int): TodoItem? = _todos.value.find { it.id == id }
}

