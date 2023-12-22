import org.gradle.kotlin.dsl.repositories

repositories {
    mavenLocal()
    mavenCentral()

    maven {
        name = "lucko"
        url = uri("https://repo.lucko.me/")
    }

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "citizens"
        url = uri("https://maven.citizensnpcs.co/repo")
    }
}