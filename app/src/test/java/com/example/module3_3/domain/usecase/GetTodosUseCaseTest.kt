package com.example.module3_3.domain.usecase

import com.example.module3_3.domain.model.TodoItem
import com.example.module3_3.domain.repository.TodoRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeRepo(var items: MutableList<TodoItem>) : TodoRepository {
    override suspend fun getTodos(): List<TodoItem> = items
    override suspend fun toggleTodo(id: Int) {
        val idx = items.indexOfFirst { it.id == id }
        if (idx >= 0) items[idx] = items[idx].copy(isCompleted = !items[idx].isCompleted)
    }
}

class GetTodosUseCaseTest {
    @Test
    fun `GetTodosUseCase returns 3 todos`() = runBlocking {
        val repo = FakeRepo(mutableListOf(
            TodoItem(1, "a", "a", false),
            TodoItem(2, "b", "b", true),
            TodoItem(3, "c", "c", false),
        ))
        val useCase = GetTodosUseCase(repo)
        val list = useCase()
        assertEquals(3, list.size)
    }
}

