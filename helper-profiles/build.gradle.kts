plugins {
    id("helper-conventions")
}

description = "helper-profiles"

dependencies {
    api("com.github.ben-manes.caffeine:caffeine:2.6.2")
    compileOnly(project(":helper"))
    compileOnly(project(":helper-sql"))
}
