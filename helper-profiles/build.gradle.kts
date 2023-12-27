import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("helper-conventions")
    id("net.minecrell.plugin-yml.paper") version("0.6.0")
}

version = "1.2.0"
description = "Provides a cached lookup service for player profiles."
project.ext.set("name", "helper-profiles")

dependencies {
    compileOnlyApi("com.github.ben-manes.caffeine:caffeine:3.1.5")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.5")
    compileOnly(project(":helper"))
    compileOnly(project(":helper-sql"))
}

paper {
    main = "me.lucko.helper.profiles.plugin.HelperProfilesPlugin"
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
        register("helper-sql") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}
