plugins {
    id("me.lucko.java-conventions")
    id("me.lucko.repo-conventions")
}

dependencies {
    api("me.lucko:scriptcontroller:1.2")
    api("io.github.classgraph:classgraph:4.6.9")
    compileOnly(project(":helper"))
}

description = "helper-js"
