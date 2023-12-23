plugins {
    id("helper-conventions")
}

description = "helper-redis"

dependencies {
    api("redis.clients:jedis:3.6.0")
    compileOnly(project(":helper"))
}
