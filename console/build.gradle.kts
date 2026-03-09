plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    mainClass.set("com.example.kotin4.CoroutinesDemoKt")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}

