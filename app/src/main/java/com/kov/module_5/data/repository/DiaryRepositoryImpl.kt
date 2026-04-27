package com.kov.module_5.data.repository

import com.kov.module_5.domain.model.DiaryEntry
import com.kov.module_5.domain.repository.DiaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class DiaryRepositoryImpl(private val filesDir: File) : DiaryRepository {

    override suspend fun getEntries(): List<DiaryEntry> = withContext(Dispatchers.IO) {
        val files = filesDir.listFiles { _, name -> name.endsWith(".txt") } ?: emptyArray()
        files.mapNotNull { file ->
            try {
                val nameWithoutExt = file.name.substringBeforeLast(".txt")
                val parts = nameWithoutExt.split("_", limit = 2)
                val timestamp = parts[0].toLongOrNull() ?: 0L
                val title = if (parts.size > 1) parts[1] else ""
                val content = file.readText()
                DiaryEntry(file.name, timestamp, title, content)
            } catch (e: Exception) {
                null
            }
        }.sortedByDescending { it.timestamp }
    }

    override suspend fun saveEntry(originalFileName: String?, title: String, content: String): DiaryEntry = withContext(Dispatchers.IO) {
        val timestamp = if (originalFileName != null) {
            originalFileName.substringBefore("_").toLongOrNull() ?: System.currentTimeMillis()
        } else {
            System.currentTimeMillis()
        }

        val safeTitle = title.replace("/", "").replace("\\", "").replace("_", " ")
        val newFileName = if (safeTitle.isNotBlank()) {
            "${timestamp}_${safeTitle}.txt"
        } else {
            "${timestamp}_.txt"
        }

        if (originalFileName != null && originalFileName != newFileName) {
            val oldFile = File(filesDir, originalFileName)
            if (oldFile.exists()) {
                oldFile.delete()
            }
        }

        val newFile = File(filesDir, newFileName)
        newFile.writeText(content)

        DiaryEntry(newFileName, timestamp, title, content)
    }

    override suspend fun deleteEntry(fileName: String) = withContext(Dispatchers.IO) {
        val file = File(filesDir, fileName)
        if (file.exists()) {
            file.delete()
        }
    }
}

