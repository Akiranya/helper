plugins {
    id("me.lucko.kotlin-conventions")
    id("me.lucko.repo-conventions")
    id("me.lucko.publishing-conventions")
}

dependencies {
    api("me.lucko:shadow-bukkit:1.20.1")
    api("com.flowpowered:flow-math:1.0.3")
    api("net.jodah:expiringmap:0.5.9")
    api("org.spongepowered:configurate-core:3.7.1")
    api("org.spongepowered:configurate-yaml:3.7.1")
    api("org.spongepowered:configurate-gson:3.7.1")
    api("org.spongepowered:configurate-hocon:3.7.1")
    api("net.kyori:text-api:3.0.4")
    api("net.kyori:text-serializer-gson:3.0.4")
    api("net.kyori:text-serializer-legacy:3.0.4")
    api("net.kyori:text-feature-pagination:3.0.4")
    api("net.kyori:text-adapter-bukkit:3.0.6")
    api("me.lucko:textlegacy:1.6.5")
    api("net.kyori:event-api:3.0.0")
    api("com.google.code.findbugs:jsr305:3.0.2")
    api("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.13.0") {
        exclude(group = "org.jetbrains.kotlin")
    }
    api("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.13.0") {
        exclude(group = "org.jetbrains.kotlin")
    }

    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0")
    compileOnly("net.citizensnpcs:citizens-main:2.0.30-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
    compileOnly("us.myles:viaversion:2.1.3")
}

description = "helper"
