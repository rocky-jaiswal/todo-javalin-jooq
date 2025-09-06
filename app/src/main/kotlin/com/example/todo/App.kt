package com.example.todo

import com.example.todo.config.Database
import com.example.todo.config.registerRoutes
import io.javalin.Javalin
import org.flywaydb.core.Flyway

fun main() {
    val db = Database()

    Flyway.configure()
        .dataSource(db.dataSource)
        .locations("classpath:db/migrations")
        .load()
        .migrate()

    val app = Javalin.create { config ->
        config.useVirtualThreads = true
        config.http.asyncTimeout = 10_000L
        config.http.defaultContentType = "application/json"
        config.showJavalinBanner = false

        registerRoutes(config, db)
    }

    val port = System.getenv("PORT")?.toIntOrNull() ?: 7000
    app.start(port)
}