plugins {
    id("java")
}

group = "one.tranic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")

    compileOnly("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
    compileOnly("org.yaml:snakeyaml:2.4")
    compileOnly("com.google.code.gson:gson:2.13.0")

    compileOnly("org.jetbrains:annotations:24.1.0")
}