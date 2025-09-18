plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)

    // Apply the application plugin to add support for building a CLI application in Java.
    application

    id("org.flywaydb.flyway") version "11.11.1"

    alias(libs.plugins.ktor)
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

    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    // BouncyCastle for PEM & Argon2id
    implementation("org.bouncycastle:bcprov-jdk18on:1.81")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.81")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("org.slf4j:slf4j-api:2.0.17")
    // Jackson for JSON encoding
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    // Logback JSON encoder
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    // Optional: Structured Logging for better MDC support
    implementation("org.slf4j:slf4j-ext:2.0.17")

    // Validation
    implementation("io.konform:konform-jvm:0.11.0")

    // DI
    implementation("io.insert-koin:koin-core:4.1.1")
    implementation("io.insert-koin:koin-logger-slf4j:4.1.1")

    // dotenv
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")

    // Ktor
    implementation(libs.ktor.server.jetty.jakarta)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.ktor.server.openapi)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)

    // http4k
//    implementation("org.http4k:http4k-core:6.17.0.0")
//    implementation("org.http4k:http4k-server-jetty:6.17.0.0")
//    implementation("org.http4k:http4k-format-jackson:6.17.0.0")

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