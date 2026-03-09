package com.example.kotin4

import kotlinx.coroutines.*
import java.io.File
import java.security.MessageDigest
import kotlin.system.measureTimeMillis

// ─── SHA-256 хеширование содержимого файла ──────────────────────────

suspend fun computeSha256(file: File): String = withContext(Dispatchers.IO) {
    val digest = MessageDigest.getInstance("SHA-256")
    val bytes = file.readBytes()
    val hash = digest.digest(bytes)
    hash.joinToString("") { "%02x".format(it) }
}

// ─── Рекурсивный поиск всех .json файлов ───────────────────────────

fun findJsonFiles(dir: File): List<File> {
    return dir.walkTopDown()
        .filter { it.isFile && it.extension.equals("json", ignoreCase = true) }
        .toList()
}

// ─── Точка входа ────────────────────────────────────────────────────

fun main(args: Array<String>) = runBlocking {

    val dirPath = if (args.isNotEmpty()) args[0] else "test-json"
    val timeoutSeconds = 10L

    val dir = File(dirPath)
    if (!dir.exists() || !dir.isDirectory) {
        println("Директория «$dirPath» не найдена.")
        println("Создайте её и положите внутрь .json файлы, или передайте путь аргументом.")
        return@runBlocking
    }

    val jsonFiles = findJsonFiles(dir)
    if (jsonFiles.isEmpty()) {
        println("В директории «$dirPath» нет .json файлов.")
        return@runBlocking
    }

    println("Найдено .json файлов: ${jsonFiles.size}")
    println("Таймаут: ${timeoutSeconds} сек")
    println("Вычисление SHA-256…\n")

    val totalTime = measureTimeMillis {

        val result = withTimeoutOrNull(timeoutSeconds * 1000) {
            // Параллельно вычисляем хеш для каждого файла
            val deferred = jsonFiles.map { file ->
                async(Dispatchers.IO) {
                    val hash = computeSha256(file)
                    file to hash
                }
            }
            deferred.awaitAll()
        }

        if (result == null) {
            println("Поиск прерван по таймауту (${timeoutSeconds} сек)")
            return@measureTimeMillis
        }

        // Группируем по хешу, оставляем только дубликаты (>1 файл с одинаковым хешем)
        val duplicates = result
            .groupBy({ it.second }, { it.first })
            .filter { it.value.size > 1 }

        // Вывод результатов
        if (duplicates.isEmpty()) {
            println("Дубликатов не найдено.")
        } else {
            println("Найдено групп дубликатов: ${duplicates.size}\n")
            duplicates.entries.forEachIndexed { index, (hash, files) ->
                println("Группа ${index + 1} (SHA-256: ${hash.take(16)}…):")
                files.forEach { println("   • ${it.path}") }
                println()
            }
        }

        // Детализация: все файлы и их хеши
        println("─── Все файлы ─────────────────────────")
        result.forEach { (file, hash) ->
            println("  ${hash.take(16)}…  ${file.path}")
        }
    }

    println("\nОбщее время выполнения: ${totalTime} мс")
}

