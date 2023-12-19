plugins {
    id("me.lucko.java-conventions")
    id("me.lucko.repo-conventions")
    id("me.lucko.publishing-conventions")
}

dependencies {
    api("com.github.ben-manes.caffeine:caffeine:2.6.2")
    compileOnly(project(":helper"))
    compileOnly(project(":helper-sql"))
}

description = "helper-profiles"
