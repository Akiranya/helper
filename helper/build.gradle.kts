import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("helper-conventions")
    id("net.minecrell.plugin-yml.paper") version ("0.6.0")
}

version = "5.6.14"
description = "A utility to reduce boilerplate code in Bukkit plugins."
project.ext.set("name", "helper")

dependencies {
    /* internal */

    val shadowVersion = "1.20.1"
    compileOnlyApi("me.lucko", "shadow-bukkit", shadowVersion)
    implementation("me.lucko", "shadow-bukkit", shadowVersion)
    val mathVersion = "1.0.3"
    compileOnlyApi("com.flowpowered", "flow-math", mathVersion)
    implementation("com.flowpowered", "flow-math", mathVersion)
    implementation("net.jodah", "expiringmap", "0.5.10")
    val configurateVersion = "3.7.3"
    implementation("org.spongepowered", "configurate-core", configurateVersion) {
        exclude("org.checkerframework", "checker-qual") // provided by Paper JAR
        exclude("com.google.guava", "guava") // provided by Paper JAR
        exclude("com.google.inject", "guice") // we don't use Guice extras
    }
    implementation("org.spongepowered", "configurate-yaml", configurateVersion) {
        exclude("org.spongepowered", "configurate-core")
        exclude("org.yaml", "snakeyaml") // provided by Paper JAR
    }
    implementation("org.spongepowered", "configurate-gson", configurateVersion) {
        exclude("org.spongepowered", "configurate-core")
        exclude("com.google.code.gson", "gson") // provided by Paper JAR
    }
    implementation("org.spongepowered", "configurate-hocon", configurateVersion) {
        exclude("org.spongepowered", "configurate-core")
        exclude("com.typesafe", "config") // provided by Kotlin JAR
    }
    implementation("net.kyori", "event-api", "3.0.0") {
        exclude("com.google.guava", "guava") // provided by Paper JAR
        exclude("org.checkerframework", "checker-qual") // provided by Paper JAR
    }
    // MCCoroutine 的 SuspendingJavaPlugin class 在每个独立插件中必须存在一个独立的 instance
    // 这是因为 MCCoroutine 所提供的 coroutine scope 是与 SuspendingJavaPlugin instance 绑定的
    val mccoroutineVersion = "2.13.0"
    compileOnlyApi("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-api", mccoroutineVersion) {
        exclude("org.jetbrains.kotlin") // provided by Kotlin JAR
        exclude("org.jetbrains.kotlinx") // provided by Kotlin JAR
    }
    implementation("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-api", mccoroutineVersion) {
        exclude("org.jetbrains.kotlin") // provided by Kotlin JAR
        exclude("org.jetbrains.kotlinx") // provided by Kotlin JAR
    }
    implementation("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-core", mccoroutineVersion) {
        exclude("org.jetbrains.kotlin") // provided by Kotlin JAR
        exclude("org.jetbrains.kotlinx") // provided by Kotlin JAR
    }

    /* 3rd party plugins */

    compileOnly("com.comphenix.protocol", "ProtocolLib", "5.0.0")
    compileOnly("us.myles", "viaversion", "2.1.3") {
        isTransitive = false
    }
    compileOnly("net.citizensnpcs", "citizens-main", "2.0.30-SNAPSHOT") {
        isTransitive = false
    }
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3") {
                    exclude("org.jetbrains.kotlin")
                    exclude("org.jetbrains.kotlinx")
                    exclude("com.google.guava", "guava")
                }
            }
        }
    }
}

tasks {
    shadowJar {
        relocate("net.kyori.event", "me.lucko.helper.eventbus")
        relocate("ninja.leaping.configurate", "me.lucko.helper.config")
        // 特意不 relocate MCCoroutine 因为 consumers 会直接 join 该 JAR 的 classpath
    }

    // to fix org.gradle.internal.execution.WorkValidationException
    // TODO better solution?
    named("copyJar") {
        mustRunAfter(project(":helper-mongo").tasks.build)
        mustRunAfter(project(":helper-profiles").tasks.build)
        mustRunAfter(project(":helper-redis").tasks.build)
        mustRunAfter(project(":helper-sql").tasks.build)
    }
}

paper {
    main = "me.lucko.helper.internal.StandalonePlugin"
    name = project.ext.get("name") as String
    version = "${project.version}"
    description = project.description
    apiVersion = "1.19"
    author = "Luck"
    website = "https://github.com/lucko/helper"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    serverDependencies {
        // Kotlin
        register("Kotlin") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        // 3rd party plugins
        register("ProtocolLib") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("ViaVersion") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Citizens") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        // Optional modules
        register("helper-js") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
        register("helper-lilypad") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
        register("helper-mongo") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
        register("helper-profiles") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
        register("helper-redis") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
        register("helper-sql") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
    }
}