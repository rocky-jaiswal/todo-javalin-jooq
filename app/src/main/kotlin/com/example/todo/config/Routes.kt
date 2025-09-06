package com.example.todo.config

import com.example.todo.controllers.AuthController
import com.example.todo.controllers.TodoController
import com.example.todo.repository.TodoRepository
import com.example.todo.repository.UserRepository
import com.example.todo.services.AuthService
import com.example.todo.services.JWTService
import com.example.todo.services.PasswordService
import com.example.todo.services.TodoService
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.config.JavalinConfig
import io.javalin.http.UnauthorizedResponse
import org.jooq.impl.DSL

fun registerRoutes(config: JavalinConfig, db: Database) {
    val userRepository = UserRepository(db.dsl())
    val todoRepository = TodoRepository(db.dsl())

    val jwtService = JWTService()
    val passwordService = PasswordService()
    val authService = AuthService(passwordService, userRepository)
    val todoService = TodoService(todoRepository)

    val authMiddleware = AuthMiddleware(jwtService)

    val authController = AuthController(jwtService, authService)
    val todoController = TodoController(todoService)

    config.router.apiBuilder {
        path("/api/health") {
            get() {
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
            before { ctx ->
                if (!authMiddleware.authenticate(ctx)) throw UnauthorizedResponse("unauthorized access")
            }
            get(todoController::list)
            post(todoController::create)
            path("{id}") {
                get(todoController::get)
                put(todoController::update)
                delete(todoController::delete)
            }
        }
    }
    // app.exception(ValidationException::class.java) { e, ctx -> todos.handleValidationErrors(e, ctx) }
}