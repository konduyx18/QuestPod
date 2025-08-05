plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    kotlin("multiplatform") version "1.9.20" apply false
    kotlin("android") version "1.9.20" apply false
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
    id("org.jetbrains.compose") version "1.5.10" apply false
    kotlin("plugin.serialization") version "1.9.20" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
