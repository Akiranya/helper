import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("helper-conventions")
    id("net.minecrell.plugin-yml.paper") version ("0.6.0")
}

version = "1.3.2"
description = "Provides SQL datasources using HikariCP."
project.ext.set("name", "helper-sql")

dependencies {
    // 由于 consumer 几乎不会直接用到 HikariCP 因此使用 implementation
    implementation("com.zaxxer", "HikariCP", "5.1.0") {
        exclude("org.slf4j", "slf4j-api")
    }
    // runtimeOnly("com.mysql", "mysql-connector-j", "8.2.0") // Paper Runtime 已经包含该依赖
    compileOnly(project(":helper"))
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
