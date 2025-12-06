package com.example.module3_3.presentation.todo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TodoListScreen(viewModel: TodoViewModel, onOpenDetail: (Int) -> Unit) {
    val todos = viewModel.todos.collectAsState().value

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(todos) { todo ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenDetail(todo.id) }
                    .padding(16.dp)) {
                    Text(text = todo.title, style = MaterialTheme.typography.titleMedium)
                    Text(text = todo.description, style = MaterialTheme.typography.bodyMedium)
                }
                Checkbox(checked = todo.isCompleted, onCheckedChange = { viewModel.onToggleTodo(todo.id) })
            }
            HorizontalDivider()
        }
    }
}
