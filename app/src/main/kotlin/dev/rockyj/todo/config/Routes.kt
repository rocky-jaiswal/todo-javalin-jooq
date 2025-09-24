package dev.rockyj.todo.config

import dev.rockyj.todo.controllers.AuthController
import dev.rockyj.todo.controllers.TodoController
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.config.JavalinConfig
import org.jooq.impl.DSL
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Routes(
    private val db: Database,
    private val authController: AuthController,
    private val todoController: TodoController) {

    private val logger: Logger = LoggerFactory.getLogger(Routes::class.java)

    fun registerRoutes(config: JavalinConfig) {
        config.router.apiBuilder {
            path("/api/public/health") {
                before {
                        ctx -> logger.info("1")
                }
                before {
                        ctx -> logger.info("2")
                }
                before {
                        ctx -> logger.info("3")
                }
                get() {
                    // StructuredLogging.LoggingUtils.logApiCall(logger, "/health", "get", null, null)

                    val ts = db.dsl()
                        .select(DSL.currentTimestamp())
                        .fetchOne(DSL.currentTimestamp())!!

                    it.json(mapOf("status" to "ok", "date" to ts.toString()))
                }
            }

            path ("/api/auth") {
                path("/register") { post(authController::register) }
                path("/login") { post(authController::login) }
            }

            path("/api/todos") {
                get(todoController::list)
                post(todoController::create)
                path("{id}") {
                    get(todoController::get)
                    // put(todoController::update)
                    delete(todoController::delete)
                }
            }
        }
    }
}

