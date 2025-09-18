package com.example.todo

import com.example.todo.config.Database
import com.example.todo.config.Routes
import com.example.todo.config.Secrets
import com.example.todo.config.appModule
import com.example.todo.middlewares.AuthMiddleware
import com.example.todo.middlewares.ExceptionHandler
import com.example.todo.middlewares.RequestLogging
import io.javalin.Javalin
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import org.koin.logger.slf4jLogger


private fun buildApp(database: Database, routes: Routes): Javalin {
    val app = Javalin.create { config ->
        config.useVirtualThreads = true
        config.http.asyncTimeout = 10_000L
        config.http.defaultContentType = "application/json"
        config.showJavalinBanner = false

        // Flyway
        config.registerPlugin(com.example.todo.plugins.Flyway(database))

        // Enable CORS
        config.bundledPlugins.enableCors { cors ->
            cors.addRule {
                // it.allowHost("example.com", "javalin.io")
                it.anyHost()
            }
        }

        routes.registerRoutes(config)
    }

    return app
}

fun main() {
    startKoin {
        slf4jLogger()
        modules(appModule)
    }

    val db : Database by inject(Database::class.java)
    val routes : Routes by inject(Routes::class.java)
    val authMiddleware : AuthMiddleware by inject(AuthMiddleware::class.java)

    val app = buildApp(db, routes)

    app
        .before(RequestLogging.beforeHandler())
        .before { ctx -> (authMiddleware::authenticate)(ctx) }
        .after(RequestLogging.afterHandler())
        .exception(Exception::class.java, ExceptionHandler.exceptionHandler())

    app.start(Secrets.APP_PORT)
}

