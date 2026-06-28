plugins {
    kotlin("jvm") version "2.3.20"
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
}

version = "1.0.0"
group = "org.kyowa"

base {
    archivesName = "FamilyAddons"
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://jitpack.io")
    maven { url = uri("https://maven.notenoughupdates.org/releases/") }
}

dependencies {
    // Un-obfuscated: no `mappings(...)` line and deps use the standard
    // `implementation`/`compileOnly` configurations (loom no longer remaps).
    // Targeting 26.1.2 (not base 26.1): base 26.1's fabric-api 0.145.1 is internally
    // inconsistent (compiles against EntityLoadData but its bundled lifecycle module
    // lacks it, and that module's mixins target methods absent from base 26.1).
    minecraft("com.mojang:minecraft:26.1.2")
    implementation("net.fabricmc:fabric-loader:0.19.3")
    implementation("net.fabricmc.fabric-api:fabric-api:0.153.0+26.1.2")
    implementation("net.fabricmc:fabric-language-kotlin:1.13.9+kotlin.2.3.10")
    compileOnly("com.terraformersmc:modmenu:20.0.0-beta.4")
    implementation("org.notenoughupdates.moulconfig:modern-26.1:4.7.2")
    include("org.notenoughupdates.moulconfig:modern-26.1:4.7.2")
}

loom {
    accessWidenerPath = file("src/main/resources/familyaddons.accesswidener")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

// Use a Java 25 toolchain so the build compiles with JDK 25 regardless of which
// JVM launches Gradle (fixes "release version 25 not supported" when the Gradle
// JVM is older). Gradle auto-detects the installed JDK 25.
kotlin {
    jvmToolchain(25)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

// Un-obfuscated 26.1: the final artifact is produced by `jar` (no `remapJar`).
tasks.jar {
    archiveVersion.set("26.1.2")
}
