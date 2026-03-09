package com.example.kotin4.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kotin4.data.RepoLoader
import com.example.kotin4.data.Repository
import kotlinx.coroutines.*

private const val DEBOUNCE_MS = 300L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoSearchScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<Repository>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var allRepos by remember { mutableStateOf<List<Repository>>(emptyList()) }

    // Job для debounce-поиска — отменяется при каждом новом вводе
    var searchJob by remember { mutableStateOf<Job?>(null) }

    // Загрузка всех репо при первом запуске
    LaunchedEffect(Unit) {
        isLoading = true
        val loaded = async(Dispatchers.IO) { RepoLoader.loadAll(context) }
        allRepos = loaded.await()
        results = allRepos
        isLoading = false
    }

    // UI
    Column(modifier = modifier.fillMaxSize()) {

        OutlinedTextField(
            value = query,
            onValueChange = { newText ->
                query = newText

                // Отменяем предыдущий поиск (cancel предыдущей Job)
                searchJob?.cancel()

                // Запускаем новый с debounce
                searchJob = scope.launch {
                    isLoading = true
                    delay(DEBOUNCE_MS)// debounce
                    val found = withContext(Dispatchers.Default) {
                        RepoLoader.search(newText, allRepos)
                    }
                    results = found
                    isLoading = false
                }
            },
            label = { Text("Поиск репозиториев…") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Индикатор загрузки
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        }

        // Список результатов
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(results, key = { it.id }) { repo ->
                RepoCard(repo)
            }
        }
    }
}

// Карточка репозитория
@Composable
private fun RepoCard(repo: Repository) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = repo.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (!repo.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = repo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "⭐ ${repo.stargazersCount}",
                    style = MaterialTheme.typography.labelMedium
                )
                repo.language?.let { lang ->
                    Text(
                        text = lang,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

