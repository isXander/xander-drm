pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuStonecutter Snapshots" }
        gradlePluginPortal()
        mavenCentral()
    }
}
plugins {
    id("dev.kikugie.stonecutter") version "0.7-alpha.16"
}

stonecutter {
    create(rootProject, file("versions/versions.json"))
}

rootProject.name = "xander-drm-mod"

include("api")