package com.example.todo.plugins

import com.example.todo.config.Database
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