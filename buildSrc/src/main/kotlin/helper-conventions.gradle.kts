plugins {
    kotlin("jvm")
    id("net.kyori.indra")
    id("com.gradleup.shadow")
    `java-library`
    `maven-publish`
}

val local = the<org.gradle.accessors.dm.LibrariesForLocal>()

group = "me.lucko"

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
    // 必须设置项目的 JVM 版本，否则将无法依赖 Paper API
    // 当然，indra 只是提供了另一种方式设置 JVM 版本
    // 用 Gradle 官方自带的 Java Tool Chain 也行
    javaVersions().target(21)
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain(21)
    sourceSets {
        val main by getting {
            dependencies {
                compileOnly(kotlin("stdlib"))
                compileOnly(local.kotlinx.coroutines.core)
            }
        }
        val test by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(local.kotlinx.coroutines.core)
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
                "-Xlint:-deprecation",
                "-Xlint:-rawtypes",
                "-Xlint:-unchecked",
            )
        )
    }
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier.set("shaded")
        dependencies {
            exclude("META-INF/maven/**")
            exclude("META-INF/versions/**")
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
}