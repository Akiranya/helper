plugins {
    id("me.lucko.java-conventions")
    id("me.lucko.repo-conventions")
    id("me.lucko.publishing-conventions")
}

dependencies {
    api("lilypad.client.connect:api:0.0.1-SNAPSHOT")
    compileOnly(project(":helper"))
}

description = "helper-lilypad"
