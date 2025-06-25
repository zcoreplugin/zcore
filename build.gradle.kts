import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm") version "2.1.20"
    id("com.gradleup.shadow") version "8.3.6"
}

group = "me.zavdav.zcore"
version = "0.14.3"

repositories {
    mavenCentral()
    maven("https://repository.johnymuffin.com/repository/maven-public/")
    maven("https://libraries.minecraft.net/")
}

dependencies {
    // Kotlin dependencies
    implementation(kotlin("stdlib-jdk8", "2.1.20"))
    testImplementation(kotlin("test", "2.1.20"))

    implementation("com.legacyminecraft.poseidon:poseidon-craftbukkit:1.1.10-250328-1731-f67a8e3")
    implementation("com.mojang:brigadier:1.0.18")
    implementation("org.jetbrains.exposed:exposed-core:0.61.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.61.0")
    implementation("com.h2database:h2:2.2.224")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
}

tasks.shadowJar {
    dependencies {
        exclude(dependency("com.legacyminecraft.poseidon:poseidon-craftbukkit:1.1.10-250328-1731-f67a8e3"))
    }
    archiveClassifier = ""
}