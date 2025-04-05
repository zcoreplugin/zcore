import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm") version "2.1.20"
}

group = "me.zavdav.zcore"
version = "0.0.3"

repositories {
    mavenCentral()
    maven("https://repository.johnymuffin.com/repository/maven-public/")
}

dependencies {
    // Kotlin dependencies
    implementation(kotlin("stdlib-jdk8", "2.1.20"))
    testImplementation(kotlin("test", "2.1.20"))

    implementation("com.legacyminecraft.poseidon:poseidon-craftbukkit:1.1.10-250328-1731-f67a8e3")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}