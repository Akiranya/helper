import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("helper-conventions")
    id("net.minecrell.plugin-yml.paper") version ("0.6.0")
}

version = "1.2.0"
project.ext.set("name", "helper-mongo")
description = "Provides MongoDB datasources."

dependencies {
    val morphiaVersion = "1.3.2"
    compileOnlyApi("org.mongodb.morphia", "morphia", morphiaVersion) {
        isTransitive = false
    }
    implementation("org.mongodb.morphia", "morphia", morphiaVersion) {
        isTransitive = false
    }
    implementation("org.mongodb", "mongo-java-driver", "3.12.12")
    compileOnly(project(":helper"))
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
}

paper {
    main = "me.lucko.helper.mongo.plugin.HelperMongoPlugin"
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
