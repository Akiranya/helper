plugins {
    id("me.lucko.kotlin-conventions")
    id("me.lucko.repo-conventions")
    id("me.lucko.publishing-conventions")
}

dependencies {
    api("me.lucko:sql-streams:1.0.0")
    api("com.zaxxer:HikariCP:4.0.3")
    api("mysql:mysql-connector-java:8.0.28")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly(project(":helper"))
}

description = "helper-sql"
