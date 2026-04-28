package com.kov.module_5.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kov.module_5.data.model.TaskEntity

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}

