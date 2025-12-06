package com.example.module3_3.presentation.todo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TodoDetailScreen(viewModel: TodoViewModel, todoId: Int, onBack: () -> Unit) {
    val todo = viewModel.getTodoById(todoId)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        if (todo == null) {
            Text("Задача не найдена")
        } else {
            Text(todo.title, style = MaterialTheme.typography.headlineSmall)
            Text("\n${todo.description}", style = MaterialTheme.typography.bodyMedium)
            Text("\nСтатус: ${if (todo.isCompleted) "Выполнено" else "В процессе"}", style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
                Text("Назад")
            }
        }
    }
}

