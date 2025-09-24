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

    // Tests
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("io.javalin:javalin-testtools:6.2.0")

    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.testcontainers:postgresql:1.19.1")
    testImplementation("org.flywaydb:flyway-core:11.11.1")
    testImplementation("org.postgresql:postgresql:42.7.7")
}

// Configure source sets
sourceSets {
    // Create integration test source set
    create("integrationTest") {
        kotlin {
            srcDir("src/integrationTest/kotlin")
        }
        resources {
            srcDir("src/integrationTest/resources")
        }
        compileClasspath += sourceSets.main.get().output + configurations.testRuntimeClasspath.get()
        runtimeClasspath += output + compileClasspath
    }
}

// Configure test tasks
tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    // Only run unit tests (exclude integration tests)
    exclude("**/*IntegrationTest*")
    exclude("**/*IT*")
}

// Create integration test task
val integrationTest = tasks.register<Test>("integrationTest") {
    description = "Runs integration tests"
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath

    environment("APP_ENV", "test")

    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }

    // Only run integration tests
    include("**/*IntegrationTest*")
    include("**/*IT*")

    // Run after unit tests
    shouldRunAfter(tasks.test)
}

// Create a task to run all tests
tasks.register("testAll") {
    description = "Runs all tests (unit and integration)"
    group = "verification"
    dependsOn(tasks.test, integrationTest)
}

// Make check depend on integration tests too
tasks.check {
    dependsOn(integrationTest)
}


// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Define the main class for the application.
    mainClass = "dev.rockyj.todo.AppKt"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}