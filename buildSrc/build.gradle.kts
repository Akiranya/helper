plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("net.kyori.indra", "net.kyori.indra.gradle.plugin", "3.1.+")
    implementation("com.gradleup.shadow", "com.gradleup.shadow.gradle.plugin", "8.3.0")
    val kotlinVersion = "2.0.10"
    implementation("org.jetbrains.kotlin.jvm", "org.jetbrains.kotlin.jvm.gradle.plugin", kotlinVersion)
    implementation("org.jetbrains.kotlin.plugin.serialization", "org.jetbrains.kotlin.plugin.serialization.gradle.plugin", kotlinVersion)
    implementation("org.jetbrains.kotlin.plugin.atomicfu", "org.jetbrains.kotlin.plugin.atomicfu.gradle.plugin", kotlinVersion)
}