import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("helper-conventions")
    id("net.minecrell.plugin-yml.paper") version("0.6.0")
}

version = "1.2.1"
description = "Provides Redis clients and implements the helper Messaging system using Jedis."
project.ext.set("name", "helper-redis")

dependencies {
    compileOnlyApi("redis.clients:jedis:3.10.0")
    implementation("redis.clients:jedis:3.10.0")
    compileOnly(project(":helper"))
}

paper {
    main = "me.lucko.helper.redis.plugin.HelperRedisPlugin"
    name = project.ext.get("name") as String
    version = "${project.version}"
    description = project.description
    apiVersion = "1.19"
    author = "Luck"
    serverDependencies {
        register("helper") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}