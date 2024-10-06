dependencyResolutionManagement {
    versionCatalogs {
        create("local") {
            from(files("gradle/local.versions.toml"))
        }
    }
}

rootProject.name = "helper-parent"

include(":helper")
include(":helper-sql")
include(":helper-redis")
include(":helper-mongo") // TODO too old, update it
include(":helper-profiles")
