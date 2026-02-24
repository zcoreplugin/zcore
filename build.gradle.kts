plugins {
    id("java")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

group = "me.zavdav"
version = "0.23.2"

repositories {
    mavenCentral()
    maven("https://repository.johnymuffin.com/repository/maven-public/")
    maven("https://libraries.minecraft.net/")
    maven("https://jitpack.io/")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    compileOnly(libs.tsunami)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.h2)
    implementation(libs.brigadier)
    compileOnly(libs.jperms)
    compileOnly(libs.essentials.libs)
    implementation(libs.slf4j.nop)
}

kotlin {
    jvmToolchain(8)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    expand(mapOf(
        "version" to project.version
    ))
}

tasks.shadowJar {
    archiveClassifier = ""
}