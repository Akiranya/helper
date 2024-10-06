import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("nyaadanbou-conventions.copy-jar")
    id("helper-conventions")
    alias(local.plugins.pluginyml.paper)
}

version = "6.0.0-SNAPSHOT"
description = "A utility to reduce boilerplate code in Bukkit plugins."

dependencies {
    compileOnly(local.paper)
    api(local.flowmath)
    implementation(local.expiringmap)
    implementation(local.configurate.core) {
        exclude("org.checkerframework", "checker-qual")
        exclude("com.google.guava", "guava")
        exclude("com.google.inject", "guice")
    }
    implementation(local.configurate.yaml) {
        exclude("org.yaml", "snakeyaml")
    }
    implementation(local.configurate.gson) {
        exclude("com.google.code.gson", "gson")
    }
    implementation(local.configurate.hocon) {
        exclude("com.typesafe", "config")
    }
    implementation(local.eventbus) {
        exclude("com.google.guava", "guava")
        exclude("org.checkerframework", "checker-qual")
    }
    api(local.mccoroutine.api) {
        exclude("org.jetbrains.kotlin")
        exclude("org.jetbrains.kotlinx")
    }
    implementation(local.mccoroutine.core) {
        exclude("org.jetbrains.kotlin")
        exclude("org.jetbrains.kotlinx")
    }
}

tasks {
    shadowJar {
        relocate("net.kyori.event", "me.lucko.helper.eventbus")
        relocate("ninja.leaping.configurate", "me.lucko.helper.config")
        // 特意不 relocate MCCoroutine 因为 consumers 会直接 join 该 JAR 的 classpath
    }
    copyJar {
        environment = "paper"
        jarFileName = "helper-${project.version}.jar"
    }
}

paper {
    main = "me.lucko.helper.internal.StandalonePlugin"
    name = "helper"
    version = "${project.version}"
    description = project.description
    apiVersion = "1.21"
    author = "Luck"
    website = "https://github.com/lucko/helper"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    serverDependencies {
        register("helper-mongo") {
            required = false
        }
        register("helper-profiles") {
            required = false
        }
        register("helper-redis") {
            required = false
        }
        register("helper-sql") {
            required = false
        }
    }
}