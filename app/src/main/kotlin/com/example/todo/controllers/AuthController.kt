package com.example.todo.controllers

import com.example.todo.dto.LoginRequest
import com.example.todo.dto.RegisterRequest
import com.example.todo.dto.loginRequestValidator
import com.example.todo.dto.userRequestValidator
import com.example.todo.services.AuthService
import com.example.todo.services.JWTService
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.bodyValidator
import io.konform.validation.Valid

class AuthController(private val jwt: JWTService, private val authService: AuthService) {

    fun register(ctx: Context) {
        val body = ctx.bodyValidator<RegisterRequest>().get()
        val validationResult = userRequestValidator.validate(body)
        if (!validationResult.isValid) {
            throw BadRequestResponse("bad request")
        }
        val req = (validationResult as Valid<RegisterRequest>).value
        authService.register(req.email, req.password)
        ctx.status(201)
    }

    fun login(ctx: Context) {
        val body = ctx.bodyValidator<LoginRequest>().get()
        val validationResult = loginRequestValidator.validate(body)
        if (!validationResult.isValid) {
            throw BadRequestResponse("bad request")
        }

        val req = (validationResult as Valid<LoginRequest>).value
        val userId = authService.verify(req.email, req.password)

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