plugins {
    id("java")
    id("maven-publish")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

group = "me.zavdav"
version = "0.24.1"

repositories {
    mavenCentral()
    maven("https://repository.johnymuffin.com/repository/maven-public/")
}

dependencies {
    shadow(libs.poseidon)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.h2)
    compileOnly(libs.jperms)
    compileOnly(libs.essentials.libs)
}

kotlin {
    jvmToolchain(25)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    expand(mapOf(
        "version" to project.version
    ))
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    dependencies {
        include(dependency(libs.exposed.core))
        include(dependency(libs.exposed.jdbc))
        include(dependency(libs.exposed.dao))
        include(dependency(libs.h2))
    }
    archiveClassifier = ""
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["shadow"])
            artifact(tasks.kotlinSourcesJar)
        }
    }
}