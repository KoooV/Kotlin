package com.example.module3_3.data.datasource

import android.content.Context
import com.example.module3_3.data.dto.TodoItemDto
import org.json.JSONArray

class TodoJsonDataSource(private val context: Context) {

    fun getTodos(): List<TodoItemDto> {
        val json = context.assets.open("todos.json").bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        val result = mutableListOf<TodoItemDto>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val id = obj.getInt("id")
            val title = obj.getString("title")
            val description = obj.getString("description")
            val isCompleted = obj.getBoolean("isCompleted")
            result.add(TodoItemDto(id, title, description, isCompleted))
        }
        return result
    }
}
