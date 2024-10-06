import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("nyaadanbou-conventions.copy-jar")
    id("helper-conventions")
    alias(local.plugins.pluginyml.paper)
}

version = "1.3.2"
description = "Provides SQL datasources using HikariCP."

dependencies {
    compileOnly(project(":helper"))
    compileOnly(local.paper)
    implementation(local.hikaricp) { exclude("org.slf4j", "slf4j-api") }
    runtimeOnly(local.mysql.connector.java)
}

tasks {
    shadowJar {
        exclude("/INFO_*")
        exclude("/README")
        exclude("/LICENSE")
        exclude("/google/**")

        val shadePattern = "me.lucko.helper.sql.external."
        relocate("com.zaxxer.hikari", shadePattern + "hikari")
        relocate("com.google.protobuf", shadePattern + "protobuf")
        relocate("be.bendem.sqlstreams", "me.lucko.helper.sql.streams")
    }
    copyJar {
        environment = "paper"
        jarFileName = "helper-sql-${project.version}.jar"
    }
}

paper {
    main = "me.lucko.helper.sql.plugin.HelperSqlPlugin"
    name = "helper-sql"
    version = "${project.version}"
    description = project.description
    apiVersion = "1.21"
    author = "Luck"
    serverDependencies {
        register("helper") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}
