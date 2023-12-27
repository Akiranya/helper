import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("helper-conventions")
    id("net.minecrell.plugin-yml.paper") version("0.6.0")
}

version = "1.3.2"
description = "Provides SQL datasources using HikariCP."
project.ext.set("name", "helper-sql")

dependencies {
    compileOnlyApi("com.zaxxer:HikariCP:5.0.1")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.mysql:mysql-connector-j:8.2.0")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly(project(":helper"))
}

paper {
    main = "me.lucko.helper.sql.plugin.HelperSqlPlugin"
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
