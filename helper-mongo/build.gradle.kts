import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("nyaadanbou-conventions.copy-jar")
    id("helper-conventions")
    alias(local.plugins.pluginyml.paper)
}

version = "1.2.0"
description = "Provides MongoDB datasources."

dependencies {
    compileOnly(project(":helper"))
    compileOnly(local.paper)
    api(local.morphia) { isTransitive = false }
    implementation(local.mongo.java.driver)
}

tasks {
    shadowJar {
        exclude("/asm-license.txt")
        exclude("/LICENSE")
        exclude("/NOTICE")

        val shadePattern = "me.lucko.helper.mongo.external."
        relocate("com.mongodb", shadePattern + "mongodriver")
        relocate("org.mongodb.morphia", shadePattern + "morphia")
        relocate("org.bson", shadePattern + "bson")
    }
    copyJar {
        environment = "paper"
        jarFileName = "helper-mongo-${project.version}.jar"
    }
}

paper {
    main = "me.lucko.helper.mongo.plugin.HelperMongoPlugin"
    name = "helper-mongo"
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
