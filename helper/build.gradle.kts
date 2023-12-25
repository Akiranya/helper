import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("helper-conventions")
    id("net.minecrell.plugin-yml.paper") version("0.6.0")
}

version = "5.6.14"
description = "A utility to reduce boilerplate code in Bukkit plugins."
project.ext.set("name", "helper")

dependencies {
    // 一般情况该模块的 consumer 不会用到这些 internal 库
    // 所以就全部设置成 implementation 了
    // 避免 IDE 自动补全有太多无用的东西

    // internal
    implementation("me.lucko", "shadow-bukkit", "1.20.1")
    implementation("com.flowpowered", "flow-math", "1.0.3")
    implementation("net.jodah", "expiringmap", "0.5.10")
    implementation("org.spongepowered", "configurate-core", "3.7.1")
    implementation("org.spongepowered", "configurate-yaml", "3.7.1")
    implementation("org.spongepowered", "configurate-gson", "3.7.1")
    implementation("org.spongepowered", "configurate-hocon", "3.7.1")
    implementation("net.kyori", "adventure-text-serializer-gson", "4.14.0")
    implementation("net.kyori", "adventure-text-serializer-legacy", "4.14.0")
    implementation("net.kyori", "event-api", "3.0.0")
    val excludeKotlin: (ExternalModuleDependency) -> Unit = { it.exclude(group = "org.jetbrains.kotlin") }
    implementation("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-api", "2.13.0", dependencyConfiguration = excludeKotlin)
    implementation("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-core", "2.13.0", dependencyConfiguration = excludeKotlin)

    // 3rd party plugins
    compileOnly("us.myles", "viaversion", "2.1.3")
    compileOnly("com.comphenix.protocol", "ProtocolLib", "5.0.0")
    compileOnly("net.citizensnpcs", "citizens-main", "2.0.30-SNAPSHOT") {
        isTransitive = false
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
        register("ViaVersion"){
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