package com.kov.module_5.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kov.module_5.domain.model.Task
import com.kov.module_5.presentation.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val completedColorEnabled by viewModel.completedColorEnabled.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TodoList") },
                actions = {
                    Text("Цвет завершенных", style = MaterialTheme.typography.bodySmall)
                    Switch(
                        checked = completedColorEnabled,
                        onCheckedChange = { viewModel.toggleCompletedColor() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { viewModel.importTasks() }) {
                        Text("Import")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    completedColorEnabled = completedColorEnabled,
                    onToggle = { viewModel.toggleTaskCompletion(task) },
                    onDelete = { viewModel.deleteTask(task) }
                )
            }
        }

        if (showDialog) {
            var title by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add Task") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") }
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.addTask(title, description)
                        showDialog = false
                    }) {
                        Text("Add")
                    }
                }
            )
        }
    }
}

@Composable
fun TaskItem(task: Task, completedColorEnabled: Boolean, onToggle: () -> Unit, onDelete: () -> Unit) {
    val bgColor = if (task.isCompleted && completedColorEnabled) Color.Green.copy(alpha = 0.2f) else Color.Transparent
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

