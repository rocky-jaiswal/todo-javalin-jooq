plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)

    // Apply the application plugin to add support for building a CLI application in Java.
    application

    id("org.flywaydb.flyway") version "11.11.1"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Javalin + validation bundle
    implementation("io.javalin:javalin:6.7.0")
    implementation("io.javalin:javalin-bundle:6.7.0")

    // jOOQ runtime
    implementation("org.jooq:jooq:3.20.6")

    // Flyway
    implementation("org.flywaydb:flyway-core:11.11.1")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:11.11.1")

    // Database - Postgres
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("com.zaxxer:HikariCP:7.0.2")

    // JWT + Bcrypt
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("com.nimbusds:nimbus-jose-jwt:10.4.2")

    // BouncyCastle for PEM & Argon2id
    implementation("org.bouncycastle:bcprov-jdk18on:1.81")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.81")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.18")

    // Tests
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("io.javalin:javalin-testtools:6.2.0")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Define the main class for the application.
    mainClass = "com.example.todo.AppKt"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}