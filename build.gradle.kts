plugins {
    id("java")
    kotlin("jvm") version "2.2.10"
    id("com.gradleup.shadow") version "8.3.6"
}

group = "me.zavdav.zcore"
version = "0.22.7"

repositories {
    mavenCentral()
    maven("https://repository.johnymuffin.com/repository/maven-public/")
    maven("https://libraries.minecraft.net/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8", "2.2.10"))
    compileOnly("com.legacyminecraft.poseidon:poseidon-craftbukkit:1.1.10-250328-1731-f67a8e3")
    implementation("com.mojang:brigadier:1.0.18")
    implementation("org.jetbrains.exposed:exposed-core:0.61.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.61.0")
    implementation("com.h2database:h2:2.2.224")
    implementation("org.slf4j:slf4j-nop:2.0.17")
    compileOnly("com.johnymuffin.jperms:beta:1.0.1")
    compileOnly("com.earth2me.essentials:essentials-libraries-rollup:0.0.1")
}

kotlin {
    jvmToolchain(8)
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
}

tasks.shadowJar {
    archiveClassifier = ""
}