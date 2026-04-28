package com.kov.module_5.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kov.module_5.domain.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean
)

fun TaskEntity.toDomain() = Task(id, title, description, isCompleted)
fun Task.toEntity() = TaskEntity(id, title, description, isCompleted)

