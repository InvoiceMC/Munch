plugins {
    kotlin("jvm") version "1.9.22"
    id("me.champeau.jmh") version "0.7.2"
}

group = "me.outspending"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))

    // SQLite
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")

    // Reflections
    implementation("org.reflections:reflections:0.10.2")

    // JMH
    jmh("org.openjdk.jmh:jmh-core:0.9")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:0.9")
    jmh("org.openjdk.jmh:jmh-generator-bytecode:0.9")
}

kotlin {
    jvmToolchain(17)
}