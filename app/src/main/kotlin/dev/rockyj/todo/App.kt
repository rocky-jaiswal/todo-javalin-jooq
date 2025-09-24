package dev.rockyj.todo

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import dev.rockyj.todo.config.Database
import dev.rockyj.todo.config.Routes
import dev.rockyj.todo.config.Secrets
import dev.rockyj.todo.config.appModule
import dev.rockyj.todo.middlewares.AuthMiddleware
import dev.rockyj.todo.middlewares.ExceptionHandler
import dev.rockyj.todo.middlewares.RequestLogging
import dev.rockyj.todo.plugins.Flyway
import io.javalin.Javalin
import io.javalin.json.JavalinJackson
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import org.koin.logger.slf4jLogger
import java.util.*

private fun buildApp(
    database: Database,
    routes: Routes,
): Javalin {
    val app =
        Javalin.create { config ->
            config.useVirtualThreads = true
            config.http.asyncTimeout = 10_000L
            config.jsonMapper(
                JavalinJackson().updateMapper { mapper ->
                    mapper.registerModule(JavaTimeModule()).setTimeZone(TimeZone.getTimeZone("UTC"))
                    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                },
            )
            config.http.defaultContentType = "application/json"
            config.showJavalinBanner = false

            // Flyway
            config.registerPlugin(Flyway(database))

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

    val db: Database by inject(Database::class.java)
    val routes: Routes by inject(Routes::class.java)
    val authMiddleware: AuthMiddleware by inject(AuthMiddleware::class.java)

    val app = buildApp(db, routes)

    app
        .before(RequestLogging.beforeHandler())
        .before { ctx -> (authMiddleware::authenticate)(ctx) }
        .after(RequestLogging.afterHandler())
        .exception(Exception::class.java, ExceptionHandler.exceptionHandler())

    app.start(Secrets.APP_PORT)
}
