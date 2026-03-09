package com.example.kotin4.data

//Сырые модели (из JSON)

data class SocialPost(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val avatarUrl: String
)

data class Comment(
    val postId: Int,
    val id: Int,
    val name: String,
    val body: String
)

//Состояние загрузки данных (для аватара и комментариев)

sealed class LoadState<out T> {
    data object Loading : LoadState<Nothing>()
    data class Ready<T>(val data: T) : LoadState<T>()
    data class Error(val message: String) : LoadState<Nothing>()
}

//Модель карточки поста для UI

data class PostCardState(
    val post: SocialPost,
    val avatarState: LoadState<String> = LoadState.Loading,   // avatarUrl или цвет
    val commentsState: LoadState<List<Comment>> = LoadState.Loading
)

