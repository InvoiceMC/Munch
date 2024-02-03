plugins {
    kotlin("jvm") version "1.9.22"
}

group = "me.outspending"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
}

kotlin {
    jvmToolchain(17)
}