plugins {
    kotlin("jvm") version "2.0.20"
}

group = "me.zavdav.zcore"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(8)
}