dependencyResolutionManagement {
    versionCatalogs {
        create("local") {
            from(files("../gradle/local.versions.toml"))
        }
    }
}