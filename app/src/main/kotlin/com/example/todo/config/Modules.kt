package com.example.todo.config

import com.example.todo.commands.AuthCommand
import com.example.todo.commands.TodoCommand
import com.example.todo.controllers.AuthController
import com.example.todo.controllers.TodoController
import com.example.todo.middlewares.AuthMiddleware
import com.example.todo.repository.TodoRepository
import com.example.todo.repository.UserRepository
import com.example.todo.services.AuthService
import com.example.todo.services.JWTService
import com.example.todo.services.PasswordService
import com.example.todo.services.TodoService
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