plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    mainClass.set("com.example.kotin4.CoroutinesDemoKt")
}

tasks.register<JavaExec>("runDuplicateFinder") {
    group = "application"
    description = "Запуск задания 2 — поиск дубликатов JSON-файлов"
    mainClass.set("com.example.kotin4.DuplicateFinderKt")
    classpath = sourceSets["main"].runtimeClasspath
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}

