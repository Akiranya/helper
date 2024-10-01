plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.atomicfu")
    id("net.kyori.indra")
    id("com.gradleup.shadow")
    `java-library`
    `maven-publish`
}

group = "me.lucko"

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.20.4-R0.1-SNAPSHOT")
}

repositories {
    mavenLocal()
    mavenCentral()

    maven {
        name = "nyaadanbou"
        url = uri("https://repo.mewcraft.cc/releases/")
    }

    maven {
        name = "lucko"
        url = uri("https://repo.lucko.me/")
    }

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "citizens"
        url = uri("https://maven.citizensnpcs.co/repo")
    }
}

indra {
    // See: https://github.com/KyoriPowered/indra/wiki/indra

    // 必须设置项目的 JVM 版本，否则将无法依赖 Paper API
    // 当然，indra 只是提供了另一种方式设置 JVM 版本
    // 用 Gradle 官方自带的 Java Tool Chain 也行
    javaVersions().target(17)
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain(17)

    sourceSets {
        val main by getting {
            dependencies {
                /*
                    由于我们依赖第三方插件 KotlinMC (Release: https://modrinth.com/plugin/kotlinmc)
                    因此我们不需要将 Kotlin 所有的运行时环境打包进我们自己的 JAR
                    因此大部分都是用的 compileOnly

                    关于是用 compileOnly 还是 implementation 的原因：
                    - compileOnly = Kotlin JAR 已提供运行时，因此编译时依赖就行，无需打包进 JAR
                    - implementation = Kotlin JAR 未提供运行时，因此不仅需要编译时依赖，还需要打包进 JAR
                */
                compileOnly(kotlin("stdlib"))
                compileOnly(kotlin("reflect"))
                compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC.2")
                compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.9.0-RC.2") {
                    exclude("com.google.guava")
                    exclude("org.jetbrains.kotlin")
                    exclude("org.jetbrains.kotlinx")
                }
                compileOnly("org.jetbrains.kotlinx:atomicfu:0.25.0")
            }
        }

        val test by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC.2")
                implementation("org.jetbrains.kotlinx:atomicfu:0.25.0")
            }
        }
    }
}

publishing {
    repositories {
        maven("https://repo.mewcraft.cc/releases/") {
            credentials {
                username = project.findProperty("nyaadanbou.mavenUsername") as String?
                password = project.findProperty("nyaadanbou.mavenPassword") as String?
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks {
    compileJava {
        options.compilerArgs.addAll(
            listOf(
                // FIXME i dunno why "-Xlint:none" not working
                "-Xlint:-deprecation",
                "-Xlint:-overrides",
                "-Xlint:-rawtypes",
                "-Xlint:-serial",
                "-Xlint:-try",
                "-Xlint:-unchecked",
                "-Xlint:-varargs",
            )
        )
    }

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
        from("${rootProject.rootDir}") {
            include("LICENSE.txt")
        }
    }

    processResources {
        from("${rootProject.rootDir}") {
            include("LICENSE.txt")
        }
    }

    javadoc {
        enabled = false
    }

    javadocJar {
        enabled = false
    }

    ////// deploy //////

    val inputJarPath by lazy { shadowJar.get().archiveFile.get().asFile.absolutePath }
    val finalJarPath by lazy { layout.buildDirectory.file(inputJarPath).get().asFile.absolutePath }

    register<Task>("deployJar") {
        group = "nyaadanbou"
        dependsOn(build)
        doLast {
            if (file(inputJarPath).exists()) {
                exec { commandLine("rsync", inputJarPath, "dev:data/dev/jar") }
            }
        }
    }
}