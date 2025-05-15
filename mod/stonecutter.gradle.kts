plugins {
    base
    id("dev.kikugie.stonecutter")
    `maven-publish`
}

stonecutter active file("versions/current")

allprojects {
    version = "1.0.0"
}

subprojects {
    apply(plugin = "maven-publish")

    publishing {
        repositories {
            val username = System.getenv("XANDER_MAVEN_USER")
            val password = System.getenv("XANDER_MAVEN_PASS")
            if (username != null && password != null) {
                maven(url = "https://maven.isxander.dev/releases") {
                    name = "XanderReleases"
                    credentials {
                        this.username = username
                        this.password = password
                    }
                }
            } else {
                logger.warn("Xander Maven credentials not satisfied.")
            }
        }
    }
}