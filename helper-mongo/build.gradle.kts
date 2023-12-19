plugins {
    id("me.lucko.java-conventions")
    id("me.lucko.repo-conventions")
    id("me.lucko.publishing-conventions")
}

dependencies {
    api("org.mongodb:mongo-java-driver:3.12.7")
    api("org.mongodb.morphia:morphia:1.3.2")
    compileOnly(project(":helper"))
}

description = "helper-mongo"
