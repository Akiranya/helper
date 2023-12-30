import net.minecrell.pluginyml.paper.PaperPluginDescription.RelativeLoadOrder

plugins {
    id("helper-conventions")
    id("net.minecrell.plugin-yml.paper") version ("0.6.0")
}

version = "1.2.0"
description = "Provides a cached lookup service for player profiles."
project.ext.set("name", "helper-profiles")

dependencies {
    implementation("com.github.ben-manes.caffeine", "caffeine", "3.1.5") {
        exclude("com.google.errorprone", "error_prone_annotations")
        exclude("org.checkerframework", "checker-qual")
    }
    compileOnly(project(":helper"))
    compileOnly(project(":helper-sql"))
}

tasks {
    shadowJar {
        val shadePattern = "me.lucko.helper.profiles.plugin.external."
        relocate("com.github.benmanes.caffeine", shadePattern + "caffeine")
    }
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
            load = RelativeLoadOrder.BEFORE
        }
        register("helper-sql") {
            required = true
            load = RelativeLoadOrder.BEFORE
        }
    }
}
