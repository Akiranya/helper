plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    val pluginSuffix = "gradle.plugin"
    implementation("net.kyori.indra", "net.kyori.indra.$pluginSuffix", "3.1.+")
    implementation("com.github.johnrengelman.shadow", "com.github.johnrengelman.shadow.$pluginSuffix", "8.1.+")
    val kotlinVersion = "1.9.10"
    implementation("org.jetbrains.kotlin.jvm", "org.jetbrains.kotlin.jvm.$pluginSuffix", kotlinVersion)
    implementation("org.jetbrains.kotlin.plugin.serialization", "org.jetbrains.kotlin.plugin.serialization.$pluginSuffix", kotlinVersion)
    implementation("org.jetbrains.kotlin.plugin.atomicfu", "org.jetbrains.kotlin.plugin.atomicfu.$pluginSuffix", kotlinVersion)
}