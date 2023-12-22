plugins {
    id("me.lucko.kotlin-conventions")
    id("me.lucko.repo-conventions")
    id("me.lucko.publishing-conventions")
}

dependencies {
    api("redis.clients:jedis:3.6.0")
    compileOnly(project(":helper"))
}

description = "helper-redis"
