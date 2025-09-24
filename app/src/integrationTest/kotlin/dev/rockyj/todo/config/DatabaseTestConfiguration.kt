package dev.rockyj.todo.config

import org.flywaydb.core.Flyway
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class DatabaseTestConfiguration {

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<Nothing> = PostgreSQLContainer<Nothing>("postgres:17-alpine").apply {
            withDatabaseName("todos_test")
            withUsername("app_test")
            withPassword("app_test")
            withReuse(true)
        }

        @JvmStatic
        val flyway: Flyway by lazy {
            Flyway.configure()
                .dataSource(postgres.jdbcUrl, postgres.username, postgres.password)
                .locations("classpath:db/migrations")
                .cleanDisabled(false)
                .load()
        }

        @JvmStatic
        fun setupDatabase() {
            if (!postgres.isRunning) {
                postgres.start()
            }

            // Run Flyway migrations
            flyway.migrate()

            // setup env. var
            System.setProperty("JDBC_URL", postgres.jdbcUrl)
        }

        @JvmStatic
        fun cleanDatabase() {
            // Clean database between tests if needed
            flyway.clean()
            flyway.migrate()
        }
    }
}