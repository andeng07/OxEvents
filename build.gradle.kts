plugins {
    id("java")
}

group = "me.centauri07.ox"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-text-minimessage:4.22.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}