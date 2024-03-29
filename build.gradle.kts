plugins {
    kotlin("jvm") version "1.9.22"
    id("me.champeau.jmh") version "0.7.2"

    `maven-publish`
}

group = "org.github.invoicemc"

version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
    implementation(kotlin("reflect"))

    // SQLite
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")

    // Reflections
    implementation("org.reflections:reflections:0.9.8")

    // JMH
    jmh("org.openjdk.jmh:jmh-core:0.9")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:0.9")
    jmh("org.openjdk.jmh:jmh-generator-bytecode:0.9")
}

publishing { publications { create<MavenPublication>("maven") { from(components["java"]) } } }

tasks {
    wrapper {
        gradleVersion = "8.6"
        distributionType = Wrapper.DistributionType.ALL
    }
}

kotlin { jvmToolchain(17) }
