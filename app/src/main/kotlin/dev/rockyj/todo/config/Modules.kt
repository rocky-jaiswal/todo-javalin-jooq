package dev.rockyj.todo.config

import dev.rockyj.todo.commands.AuthCommand
import dev.rockyj.todo.commands.TodoCommand
import dev.rockyj.todo.controllers.AuthController
import dev.rockyj.todo.controllers.TodoController
import dev.rockyj.todo.middlewares.AuthMiddleware
import dev.rockyj.todo.repository.TodoRepository
import dev.rockyj.todo.repository.UserRepository
import dev.rockyj.todo.services.AuthService
import dev.rockyj.todo.services.JWTService
import dev.rockyj.todo.services.PasswordService
import dev.rockyj.todo.services.TodoService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::Database)

    singleOf(::JWTService)
    singleOf(::PasswordService)

    singleOf(::UserRepository)
    singleOf(::TodoRepository)

    singleOf(::AuthService)
    singleOf(::TodoService)

    singleOf(::AuthMiddleware)

    singleOf(::AuthCommand)
    singleOf(::TodoCommand)

    singleOf(::AuthController)
    singleOf(::TodoController)

    singleOf(::Routes)
}