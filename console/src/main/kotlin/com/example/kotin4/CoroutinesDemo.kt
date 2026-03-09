package com.example.kotin4

import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

//Data-классы

data class User(val id: Int, val name: String)

data class SaleItem(val product: String, val qty: Int, val revenue: Int)
data class SalesReport(val today: String, val items: List<SaleItem>)

data class WeatherInfo(val city: String, val temp: Int, val condition: String)

//Эмуляция «долгих» операций

suspend fun loadUsers(): List<User> {
    delay(1800)
    maybeFail("loadUsers")
    return listOf(
        User(1, "Alice"),
        User(2, "Bob"),
        User(3, "Ivan"),
        User(4, "Olga")
    )
}

suspend fun loadSalesStatistics(): SalesReport {
    delay(1200)
    maybeFail("loadSalesStatistics")
    return SalesReport(
        today = "2025-12-01",
        items = listOf(
            SaleItem("Coffee", 42, 1680),
            SaleItem("Tea", 19, 475)
        )
    )
}

suspend fun loadWeather(): List<WeatherInfo> {
    delay(2500)
    maybeFail("loadWeather")
    return listOf(
        WeatherInfo("Moscow", -18, "snow"),
        WeatherInfo("New York", -5, "cloudy"),
        WeatherInfo("Tokyo", 11, "rain")
    )
}

private fun maybeFail(taskName: String) {
    if (Random.nextInt(100) < 20) {
        throw RuntimeException("Случайный сбой в задаче «$taskName»!")
    }
}

// Форматирование в JSON

fun List<User>.toJson(): String = buildString {
    appendLine("[")
    this@toJson.forEachIndexed { i, u ->
        val comma = if (i < this@toJson.lastIndex) "," else ""
        appendLine("""  {"id":${u.id}, "name":"${u.name}"}$comma""")
    }
    append("]")
}

fun SalesReport.toJson(): String = buildString {
    appendLine("{")
    appendLine("""  "today": "$today",""")
    appendLine("""  "items": [""")
    items.forEachIndexed { i, s ->
        val comma = if (i < items.lastIndex) "," else ""
        appendLine("""    {"product":"${s.product}", "qty":${s.qty}, "revenue":${s.revenue}}$comma""")
    }
    appendLine("  ]")
    append("}")
}

fun List<WeatherInfo>.toWeatherJson(): String = buildString {
    appendLine("[")
    this@toWeatherJson.forEachIndexed { i, w ->
        val comma = if (i < this@toWeatherJson.lastIndex) "," else ""
        appendLine("""  {"city":"${w.city}", "temp":${w.temp}, "condition":"${w.condition}"}$comma""")
    }
    append("]")
}


fun main() = runBlocking {
    println("Запуск трёх параллельных задач…\n")

    val totalTime = measureTimeMillis {
        val usersDeferred = async {
            try { loadUsers() }
            catch (e: Exception) { println("Ошибка при загрузке пользователей: ${e.message}"); null }
        }

        val salesDeferred = async {
            try { loadSalesStatistics() }
            catch (e: Exception) { println("Ошибка при загрузке статистики: ${e.message}"); null }
        }

        val weatherDeferred = async {
            try { loadWeather() }
            catch (e: Exception) { println("Ошибка при загрузке погоды: ${e.message}"); null }
        }

        val users = usersDeferred.await()
        val sales = salesDeferred.await()
        val weather = weatherDeferred.await()

        println("═══════════════ users.json ═══════════════")
        if (users != null) println(users.toJson())
        else println("данные недоступны")

        println()
        println("═══════════════ sales.json ═══════════════")
        if (sales != null) println(sales.toJson())
        else println("данные недоступны")

        println()
        println("══════════════ weather.json ══════════════")
        if (weather != null) println(weather.toWeatherJson())
        else println("данные недоступны")

        println("══════════════════════════════════════════")
    }

    println("\nОбщее время выполнения: ${totalTime} мс")
}
