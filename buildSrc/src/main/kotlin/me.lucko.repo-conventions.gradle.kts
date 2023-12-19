import org.gradle.kotlin.dsl.repositories

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.lucko.me/")
    }

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        name = "citizens-repo"
        url = uri("https://maven.citizensnpcs.co/repo")
    }
}