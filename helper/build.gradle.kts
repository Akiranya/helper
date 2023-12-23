plugins {
    id("helper-conventions")
}

description = "helper"

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
