package com.example.module3_3.domain.usecase

import com.example.module3_3.domain.model.TodoItem
import com.example.module3_3.domain.repository.TodoRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeRepo2(var items: MutableList<TodoItem>) : TodoRepository {
    override suspend fun getTodos(): List<TodoItem> = items
    override suspend fun toggleTodo(id: Int) {
        val idx = items.indexOfFirst { it.id == id }
        if (idx >= 0) items[idx] = items[idx].copy(isCompleted = !items[idx].isCompleted)
    }
}

class ToggleTodoUseCaseTest {
    @Test
    fun `toggleTodo flips isCompleted`() = runBlocking {
        val repo = FakeRepo2(mutableListOf(
            TodoItem(1, "task", "d", false)
        ))
        val useCase = ToggleTodoUseCase(repo)

        // ensure initial false
        val before = repo.getTodos().first().isCompleted
        assertFalse(before)

        useCase(1)
        val after = repo.getTodos().first().isCompleted
        assertTrue(after)
    }
}

