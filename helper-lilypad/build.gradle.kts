plugins {
    id("helper-conventions")
}

description = "helper-lilypad"

dependencies {
    api("lilypad.client.connect:api:0.0.1-SNAPSHOT")
    compileOnly(project(":helper"))
}
