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
    api("lilypad.client.connect:api:0.0.1-SNAPSHOT")
    compileOnly(project(":helper"))
}

description = "helper-lilypad"