import net.minecrell.pluginyml.paper.PaperPluginDescription.RelativeLoadOrder

plugins {
    id("nyaadanbou-conventions.copy-jar")
    id("helper-conventions")
    alias(local.plugins.pluginyml.paper)
}

version = "1.2.1"
description = "Provides Redis clients and implements the helper Messaging system using Jedis."

dependencies {
    compileOnly(project(":helper"))
    compileOnly(local.paper)
    implementation(local.jedis) { exclude("org.slf4j", "slf4j-api") }
}

tasks {
    shadowJar {
        val shadePattern = "me.lucko.helper.redis.external."
        relocate("redis.clients.jedis", shadePattern + "jedis")
        relocate("org.apache.commons.pool", shadePattern + "pool")
    }
    copyJar {
        environment = "paper"
        jarFileName = "helper-redis-${project.version}.jar"
    }
}

paper {
    main = "me.lucko.helper.redis.plugin.HelperRedisPlugin"
    name = "helper-redis"
    version = "${project.version}"
    description = project.description
    apiVersion = "1.19"
    author = "Luck"
    serverDependencies {
        register("helper") {
            required = true
            load = RelativeLoadOrder.BEFORE
        }
    }
}