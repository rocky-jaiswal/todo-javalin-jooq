package dev.rockyj.todo.plugins

import dev.rockyj.todo.config.Database
import io.javalin.config.JavalinConfig
import io.javalin.plugin.Plugin
import org.flywaydb.core.Flyway

class Flyway(private val db: Database) : Plugin<Void>() {
    override fun onInitialize(config: JavalinConfig) {
        Flyway.configure()
            .dataSource(db.dataSource)
            .locations("classpath:db/migrations")
            .load()
            .migrate()
    }
}