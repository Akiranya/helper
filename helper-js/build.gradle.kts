plugins {
    id("helper-conventions")
    id("net.minecrell.plugin-yml.paper") version("0.6.0")
}

version = "2.0.0"
description = "JavaScript plugins powered by helper."
project.ext.set("name", "helper-js")

dependencies {
    api("me.lucko:scriptcontroller:1.2")
    api("io.github.classgraph:classgraph:4.6.9")
    compileOnly(project(":helper"))
}

paper {
    main = "me.lucko.helper.js.HelperJsPlugin"
    name = project.ext.get("name") as String
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