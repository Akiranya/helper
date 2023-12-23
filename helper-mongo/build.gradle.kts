plugins {
    id("helper-conventions")
}

description = "helper-mongo"

dependencies {
    api("org.mongodb:mongo-java-driver:3.12.7")
    api("org.mongodb.morphia:morphia:1.3.2")
    compileOnly(project(":helper"))
}
