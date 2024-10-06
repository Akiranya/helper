import net.minecrell.pluginyml.paper.PaperPluginDescription.RelativeLoadOrder

plugins {
    id("nyaadanbou-conventions.copy-jar")
    id("helper-conventions")
    alias(local.plugins.pluginyml.paper)
}

version = "1.2.0"
description = "Provides a cached lookup service for player profiles."

dependencies {
    compileOnly(project(":helper"))
    compileOnly(project(":helper-sql"))
    compileOnly(local.paper)
    implementation(local.caffeine) {
        exclude("com.google.errorprone", "error_prone_annotations")
        exclude("org.checkerframework", "checker-qual")
    }
}

tasks {
    shadowJar {
        val shadePattern = "me.lucko.helper.profiles.plugin.external."
        relocate("com.github.benmanes.caffeine", shadePattern + "caffeine")
    }
    copyJar {
        environment = "paper"
        jarFileName = "helper-profiles-${project.version}.jar"
    }
}

paper {
    main = "me.lucko.helper.profiles.plugin.HelperProfilesPlugin"
    name = "helper-profiles"
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
