plugins {
    id("helper-conventions")
}

description = "helper-js"

dependencies {
    api("me.lucko:scriptcontroller:1.2")
    api("io.github.classgraph:classgraph:4.6.9")
    compileOnly(project(":helper"))
}
