import gradle.kotlin.dsl.accessors._e56c6a8408aee35f79834ef2a2113d39.compileOnly

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.atomicfu")

    id("com.github.johnrengelman.shadow")
}

tasks {
    // Kotlin source files are always UTF-8 by design.
    compileKotlin {
        dependsOn(clean)
    }

    compileTestKotlin {
        dependsOn(clean)
    }

    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("shaded")
        dependencies {
            exclude("META-INF/NOTICE")
            exclude("META-INF/maven/**")
            exclude("META-INF/versions/**")
            exclude("META-INF/**.kotlin_module")
        }
    }
}

java {
    withSourcesJar()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
}

kotlin {
    jvmToolchain(17)

    sourceSets {
        val main by getting {
            dependencies {
                // 为了支持若紫 Eco, 必须自己 shade
                implementation(kotlin("stdlib"))
                // implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.3.0")
                implementation("org.jetbrains.kotlinx:atomicfu:0.22.0")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3") {
                    exclude(group = "*")
                }
                implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.13.0") {
                    exclude(group = "org.jetbrains.kotlin")
                }
                implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.13.0") {
                    exclude(group = "org.jetbrains.kotlin")
                }
            }
        }

        val test by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
    }
}