package com.example.kotin4

import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis


/** 1. Загрузка списка пользователей (≈ 1800 мс) */
suspend fun loadUsers(): List<String> {
    delay(1800)
    maybeFail("loadUsers")
    return listOf("Алексей", "Мария", "Дмитрий", "Елена", "Сергей")
}

/** 2. Загрузка статистики продаж за день (≈ 1200 мс) */
suspend fun loadSalesStatistics(): Map<String, Int> {
    delay(1200)
    maybeFail("loadSalesStatistics")
    return mapOf(
        "Ноутбуки" to 12,
        "Смартфоны" to 47,
        "Планшеты" to 8,
        "Наушники" to 134
    )
}

/** 3. Получение текущей погоды в 3 городах (≈ 2500 мс) */
suspend fun loadWeather(): List<String> {
    delay(2500)
    maybeFail("loadWeather")
    return listOf(
        "Москва: -3°C",
        "Санкт-Петербург: -7°C",
        "Новосибирск: -18°C"
    )
}

// С вероятностью ~20 % бросает исключение, эмулируя случайный сбой.

private fun maybeFail(taskName: String) {
    if (Random.nextInt(100) < 20) {
        throw RuntimeException("Случайный сбой в задаче «$taskName»!")
    }
}

fun main() = runBlocking {
    println(" Запуск трёх параллельных задач…\n")

    val totalTime = measureTimeMillis {

        // Запускаем три задачи параллельно через async.
        // Каждая обёрнута в try-catch, чтобы при сбое вернуть null, а не уронить всю программу.
        val usersDeferred = async {
            try {
                loadUsers()
            } catch (e: Exception) {
                println(" Ошибка при загрузке пользователей: ${e.message}")
                null
            }
        }

        val salesDeferred = async {
            try {
                loadSalesStatistics()
            } catch (e: Exception) {
                println(" Ошибка при загрузке статистики продаж: ${e.message}")
                null
            }
        }

        val weatherDeferred = async {
            try {
                loadWeather()
            } catch (e: Exception) {
                println(" Ошибка при загрузке погоды: ${e.message}")
                null
            }
        }

        // Ожидаем завершения ВСЕХ трёх задач
        val users = usersDeferred.await()
        val sales = salesDeferred.await()
        val weather = weatherDeferred.await()

        println("════════════════════════════════════════")

        if (users != null) {
            println("Пользователи: ${users.joinToString()}")
        } else {
            println("Пользователи: данные недоступны (произошла ошибка)")
        }

        println()

        if (sales != null) {
            println("Статистика продаж за день:")
            sales.forEach { (product, count) ->
                println("   • $product — $count шт.")
            }
        } else {
            println("Статистика продаж: данные недоступны (произошла ошибка)")
        }

        println()

        if (weather != null) {
            println("Погода:")
            weather.forEach { println("   • $it") }
        } else {
            println("Погода: данные недоступны (произошла ошибка)")
        }

        println("════════════════════════════════════════")
    }

    println("\nОбщее время выполнения: ${totalTime} мс")
}

