plugins {
    id("dev.isxander.modstitch.base") version "0.5.15-unstable"
    id("dev.kikugie.stonecutter")
}

modstitch {
    minecraftVersion = stonecutter.current.version
    javaTarget = 21

    metadata {
        modId = "xander-drm"
        modVersion = project.version.toString()
        modName = "Xander DRM"
        modDescription = "DRM for Xander's commissioned mods."
    }

    loom {
        fabricLoaderVersion = "0.16.14"
    }
}


dependencies {
    fun Dependency?.jij() = this?.let { modstitchJiJ(it) }

    implementation(project(":api")).jij()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "dev.isxander.drm"
            artifactId = "xander-drm-mod"
            version = project.version.toString() + "+${stonecutter.current.project}"
        }
    }
}
