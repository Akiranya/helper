/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    id("me.lucko.java-conventions")
    id("me.lucko.repo-conventions")
}

dependencies {
    api("com.github.ben-manes.caffeine:caffeine:2.6.2")
    compileOnly(project(":helper"))
    compileOnly(project(":helper-sql"))
}

description = "helper-profiles"