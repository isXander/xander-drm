# Xander DRM Mod

This is the mod component of the Xander DRM system. It provides DRM functionality for Xander's commissioned mods.

## Building

To build the mod locally:

```bash
cd mod
./gradlew build
```

The built JAR files will be available in:
- `build/libs/` (main JAR)
- `versions/*/build/libs/` (version-specific JARs)

## GitHub Actions Workflows

This project includes two GitHub Actions workflows for building and publishing the mod:

### Build Workflow

The build workflow automatically runs whenever changes are pushed to the `main` branch or when a pull request is created that affects the mod code. It:

1. Sets up Java 21
2. Builds the mod using Gradle
3. Uploads the build artifacts (JAR files) for download

You can view the build results in the "Actions" tab of the GitHub repository.

### Publish Workflow

The publish workflow is triggered manually from the GitHub Actions UI. It:

1. Builds the mod
2. Publishes it to the Maven repository at `maven.isxander.dev/releases`
3. Uploads the build artifacts (JAR files) for download

To trigger the publish workflow:

1. Go to the "Actions" tab in the GitHub repository
2. Select the "Publish Mod" workflow
3. Click "Run workflow"
4. Optionally enter a custom version number (if left empty, it will use the version from build.gradle.kts)
5. Click "Run workflow" to start the publishing process

Note: The publish workflow requires the `XANDER_MAVEN_USER` and `XANDER_MAVEN_PASS` secrets to be set in the GitHub repository settings.

## Maven Repository

The mod is published to the Maven repository at `maven.isxander.dev/releases`. To use it as a dependency in other projects:

```kotlin
repositories {
    maven("https://maven.isxander.dev/releases")
}

dependencies {
    implementation("dev.isxander:xander-drm:1.0.0") // Replace with the actual version
}
```