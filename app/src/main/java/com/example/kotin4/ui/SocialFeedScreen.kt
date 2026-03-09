package com.example.kotin4.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.kotin4.data.*
import kotlinx.coroutines.*

//Цвета-заглушки для аватарок
private val avatarColors = listOf(
    Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFF5C6BC0),
    Color(0xFF29B6F6), Color(0xFF26A69A), Color(0xFF66BB6A),
    Color(0xFFFFA726), Color(0xFF8D6E63), Color(0xFFEC407A),
    Color(0xFF7E57C2), Color(0xFF42A5F5), Color(0xFF26C6DA)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    //State
    var postCards by remember { mutableStateOf<List<PostCardState>>(emptyList()) }
    var isInitialLoading by remember { mutableStateOf(true) }

    // Job для всех загрузок аватарок/комментариев — можно отменить кнопкой «Обновить»
    var loadJob by remember { mutableStateOf<Job?>(null) }

    // Кэш прочитанных комментариев из JSON (чтобы не читать файл повторно)
    var allComments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var allPosts by remember { mutableStateOf<List<SocialPost>>(emptyList()) }

    //Функция загрузки ленты
    fun loadFeed() {
        // Отменяем предыдущие загрузки (Job.cancel())
        loadJob?.cancel()

        loadJob = scope.launch {
            isInitialLoading = true

            val posts = async(Dispatchers.IO) { SocialDataLoader.loadPosts(context) }
            val comments = async(Dispatchers.IO) { SocialDataLoader.loadAllComments(context) }

            allPosts = posts.await()
            allComments = comments.await()

            // Инициализируем карточки в состоянии Loading
            postCards = allPosts.map { PostCardState(post = it) }
            isInitialLoading = false

            // Для каждого поста параллельно загружаем аватарку и комментарии
            //    supervisorScope — чтобы ошибка в одном посте не отменяла другие
            supervisorScope {
                allPosts.forEach { post ->
                    // Каждый пост — отдельная корутина
                    launch {
                        // Внутри поста — параллельная загрузка аватарки и комментариев
                        // coroutineScope — если одна подзадача упадёт, вторая тоже отменится
                        // Но мы оборачиваем каждый async в try-catch, чтобы обработать ошибки отдельно

                        val avatarDeferred = async {
                            try {
                                val url = SocialDataLoader.fetchAvatar(post.avatarUrl)
                                LoadState.Ready(url)
                            } catch (e: CancellationException) {
                                throw e  // не глотаем отмену
                            } catch (e: Exception) {
                                LoadState.Error(e.message ?: "Ошибка")
                            }
                        }

                        val commentsDeferred = async {
                            try {
                                val list = SocialDataLoader.fetchComments(post.id, allComments)
                                LoadState.Ready(list)
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                LoadState.Error(e.message ?: "Ошибка")
                            }
                        }

                        val avatarResult = avatarDeferred.await()
                        val commentsResult = commentsDeferred.await()

                        // Обновляем конкретную карточку — пост готов (по мере готовности)
                        postCards = postCards.map { card ->
                            if (card.post.id == post.id) {
                                card.copy(
                                    avatarState = avatarResult,
                                    commentsState = commentsResult
                                )
                            } else card
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) { loadFeed() }


    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Социальная лента") },
                actions = {
                    IconButton(onClick = { loadFeed() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                }
            )
        }
    ) { innerPadding ->

        if (isInitialLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Загрузка ленты…")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 8.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(postCards, key = { it.post.id }) { card ->
                    AnimatedVisibility(visible = true, enter = fadeIn()) {
                        PostCard(card)
                    }
                }
            }
        }
    }
}

//Карточка поста
@Composable
private fun PostCard(card: PostCardState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Шапка
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarView(
                    state = card.avatarState,
                    userId = card.post.userId
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.post.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "user #${card.post.userId}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Индикатор состояния поста
                PostStatusBadge(card)
            }

            Spacer(Modifier.height(8.dp))

            // Тело поста
            Text(
                text = card.post.body,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            //Коммент
            CommentsSection(card.commentsState)
        }
    }
}

//Аватарка
@Composable
private fun AvatarView(state: LoadState<String>, userId: Int) {
    val size = 48.dp
    when (state) {
        is LoadState.Loading -> {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }
        is LoadState.Ready -> {
            // Показываем цветной круг с иконкой (имитация загруженной аватарки)
            val color = avatarColors[userId % avatarColors.size]
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        is LoadState.Error -> {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Ошибка загрузки аватарки",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


//Индикатор состояния карточки
@Composable
private fun PostStatusBadge(card: PostCardState) {
    val avatar = card.avatarState
    val comments = card.commentsState

    when {
        avatar is LoadState.Loading || comments is LoadState.Loading -> {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
        }
        avatar is LoadState.Error || comments is LoadState.Error -> {
            Icon(
                Icons.Default.Warning,
                contentDescription = "Ошибка",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }
        else -> {
            // Ready — можно ничего не показывать, или галочку
            Text("✓", color = MaterialTheme.colorScheme.primary)
        }
    }
}

//Секция комментариев
@Composable
private fun CommentsSection(state: LoadState<List<Comment>>) {
    when (state) {
        is LoadState.Loading -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Загрузка комментариев…",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        is LoadState.Error -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    state.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        is LoadState.Ready -> {
            val comments = state.data
            if (comments.isEmpty()) {
                Text(
                    "Нет комментариев",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    "Комментарии (${comments.size}):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                comments.forEach { comment ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(
                            text = "${comment.name}: ",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = comment.body,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

