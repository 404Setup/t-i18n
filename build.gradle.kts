plugins {
    id("java")
}

group = "one.tranic"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.18-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:4.20.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.20.0")

    compileOnly("org.yaml:snakeyaml:2.4")
    compileOnly("com.google.code.gson:gson:2.13.0")

    compileOnly("org.jetbrains:annotations:24.1.0")
}