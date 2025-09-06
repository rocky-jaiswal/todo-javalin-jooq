package com.example.todo.controllers

import com.example.todo.dto.LoginRequest
import com.example.todo.dto.RegisterRequest
import com.example.todo.services.AuthService
import com.example.todo.services.JWTService
import io.javalin.http.Context
import io.javalin.http.bodyValidator

class AuthController(private val jwt: JWTService, private val authService: AuthService) {

    fun register(ctx: Context) {
        val body = ctx.bodyValidator<RegisterRequest>()
            .check({ it.email.isNotBlank() }, "email required")
            .check({ it.password.length >= 8 }, "password min 8")
            .get()
        authService.register(body.email, body.password)
        ctx.status(201)
    }

    fun login(ctx: Context) {
        val body = ctx.bodyValidator<LoginRequest>()
            .check({ it.email.isNotBlank() }, "email required")
            .check({ it.password.isNotBlank() }, "password required")
            .get()

        val userId = authService.verify(body.email, body.password)

        if (userId == null) {
            ctx.status(401).json(mapOf("error" to "invalid credentials"))
            return
        }

        val token = jwt.signJWT(
            "$userId",
            audience = "app",
            expirationMinutes = 60,
            customClaims = null
        )
        ctx.json(mapOf("token" to token))
    }
}