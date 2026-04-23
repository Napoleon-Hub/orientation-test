plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}

// Регистрация задачи очистки
tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}