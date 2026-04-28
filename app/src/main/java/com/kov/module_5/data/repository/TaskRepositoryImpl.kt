package com.kov.module_5.data.repository

import android.content.Context
import com.kov.module_5.R
import com.kov.module_5.data.local.TaskDao
import com.kov.module_5.data.model.TaskEntity
import com.kov.module_5.data.model.toDomain
import com.kov.module_5.data.model.toEntity
import com.kov.module_5.domain.model.Task
import com.kov.module_5.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val context: Context
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks().map { it.map { entity -> entity.toDomain() } }

    override suspend fun getTaskById(id: String): Task? = taskDao.getTaskById(id)?.toDomain()

    override suspend fun insertTask(task: Task) = taskDao.insertTask(task.toEntity())

    override suspend fun updateTask(task: Task) = taskDao.updateTask(task.toEntity())

    override suspend fun deleteTask(task: Task) = taskDao.deleteTask(task.toEntity())

    override suspend fun importTasksFromJson() {
        try {
            val inputStream = context.resources.openRawResource(R.raw.tasks)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val tasks = mutableListOf<TaskEntity>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                tasks.add(
                    TaskEntity(
                        id = jsonObject.getString("id"),
                        title = jsonObject.getString("title"),
                        description = jsonObject.getString("description"),
                        isCompleted = jsonObject.getBoolean("isCompleted")
                    )
                )
            }
            taskDao.insertTasks(tasks)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

