package com.example.kotin4.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

object RepoLoader {

    /**
     * Загружает все репозитории из assets/github_repos.json.
     * Выполняется на Dispatchers.IO.
     */
    suspend fun loadAll(context: Context): List<Repository> = withContext(Dispatchers.IO) {
        val json = context.assets.open("github_repos.json")
            .bufferedReader()
            .use { it.readText() }

        val array = JSONArray(json)
        val repos = mutableListOf<Repository>()

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            repos += Repository(
                id = obj.getInt("id"),
                fullName = obj.getString("full_name"),
                description = if (obj.isNull("description")) null else obj.getString("description"),
                stargazersCount = obj.getInt("stargazers_count"),
                language = if (obj.isNull("language")) null else obj.getString("language")
            )
        }

        repos
    }

    /**
     * Фильтрует список репозиториев по запросу (по full_name и description).
     * Выполняется на Dispatchers.Default (CPU-bound работа).
     */
    suspend fun search(
        query: String,
        repos: List<Repository>
    ): List<Repository> = withContext(Dispatchers.Default) {
        if (query.isBlank()) return@withContext repos

        val lowerQuery = query.lowercase()
        repos.filter { repo ->
            repo.fullName.lowercase().contains(lowerQuery) ||
                    (repo.description?.lowercase()?.contains(lowerQuery) == true)
        }
    }
}

