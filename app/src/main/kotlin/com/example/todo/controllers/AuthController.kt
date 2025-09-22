package com.example.todo.controllers

import com.example.todo.commands.AuthCommand
import com.example.todo.commands.LoginRequest
import com.example.todo.commands.RegisterRequest
import io.javalin.http.Context
import io.javalin.http.bodyValidator

class AuthController(private val authControllerCommand: AuthCommand) {

    fun register(ctx: Context) {
        val body = ctx.bodyValidator<RegisterRequest>().get()
        authControllerCommand.register(body)
        ctx.status(201)
    }

    fun login(ctx: Context) {
        val body = ctx.bodyValidator<LoginRequest>().get()
        val token = authControllerCommand.login(body)
        ctx.json(mapOf("token" to token))
    }
}