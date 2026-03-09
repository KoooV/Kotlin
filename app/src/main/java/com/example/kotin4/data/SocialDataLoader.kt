package com.example.kotin4.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import kotlin.random.Random

object SocialDataLoader {

    //Чтение JSON из assets

    suspend fun loadPosts(context: Context): List<SocialPost> = withContext(Dispatchers.IO) {
        val json = context.assets.open("social_posts.json")
            .bufferedReader().use { it.readText() }
        val arr = JSONArray(json)
        List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            SocialPost(
                id = o.getInt("id"),
                userId = o.getInt("userId"),
                title = o.getString("title"),
                body = o.getString("body"),
                avatarUrl = o.getString("avatarUrl")
            )
        }
    }

    suspend fun loadAllComments(context: Context): List<Comment> = withContext(Dispatchers.IO) {
        val json = context.assets.open("comments.json")
            .bufferedReader().use { it.readText() }
        val arr = JSONArray(json)
        List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            Comment(
                postId = o.getInt("postId"),
                id = o.getInt("id"),
                name = o.getString("name"),
                body = o.getString("body")
            )
        }
    }

    //Имитация загрузки аватарки

    suspend fun fetchAvatar(avatarUrl: String): String {
        // Имитируем сетевую задержку 300‒1200 мс
        delay(Random.nextLong(300, 1200))
        // С вероятностью 15 % — ошибка загрузки
        if (Random.nextInt(100) < 15) {
            throw RuntimeException("Ошибка загрузки аватарки")
        }
        return avatarUrl
    }

    //Имитация загрузки комментариев

    suspend fun fetchComments(postId: Int, allComments: List<Comment>): List<Comment> {
        // Имитируем сетевую задержку 500‒2000 мс
        delay(Random.nextLong(500, 2000))
        // С вероятностью 10 % — ошибка
        if (Random.nextInt(100) < 10) {
            throw RuntimeException("Не удалось загрузить комментарии")
        }
        return allComments.filter { it.postId == postId }
    }
}

