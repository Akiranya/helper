plugins {
    id("helper-conventions")
    id("net.minecrell.plugin-yml.paper") version("0.6.0")
}

version = "3.0.0"
description = "Implements the helper Messaging system using LilyPad."

dependencies {
    api("lilypad.client.connect:api:0.0.1-SNAPSHOT")
    compileOnly(project(":helper"))
}

paper {
    main = "me.lucko.helper.lilypad.plugin.HelperLilyPadPlugin"
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
        register("LilyPad-Connect") {
            required = true
            load = RelativeLoadOrder.BEFORE
        }
    }
}