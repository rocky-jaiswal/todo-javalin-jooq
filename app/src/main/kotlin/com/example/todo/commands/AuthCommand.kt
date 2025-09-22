package com.example.todo.commands

import com.example.todo.services.AuthService
import com.example.todo.services.JWTService
import com.example.todo.utils.failure
import com.example.todo.utils.flatMap
import com.example.todo.utils.getOrThrow
import com.example.todo.utils.ifSuccess
import com.example.todo.utils.success
import io.javalin.http.BadRequestResponse
import io.javalin.http.UnauthorizedResponse
import io.konform.validation.Invalid
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.notBlank
import io.konform.validation.constraints.pattern

data class RegisterRequest(val email: String, val password: String)

data class LoginRequest(val email: String, val password: String)

val userRequestValidator = Validation<RegisterRequest> {
    RegisterRequest::email {
        minLength(1) hint "email cannot be blank"
        pattern(Regex("(.*)@(.*)"))
    }
    RegisterRequest::password {
        minLength(6) hint "password must be 6 characters"
    }
}

val loginRequestValidator = Validation<LoginRequest> {
    LoginRequest::email {
        notBlank()
        pattern(Regex("(.*)@(.*)"))
    }
    LoginRequest::password {
        notBlank()
        minLength(6) hint "password must be 6 characters"
    }
}

class AuthCommand(private val jwt: JWTService, private val authService: AuthService) {

    fun register(registerRequest: RegisterRequest) {
        val validationResult = userRequestValidator.validate(registerRequest)

        when(validationResult) {
            is Valid -> success(validationResult.value)
            is Invalid -> failure(BadRequestResponse("bad request"))
        }.ifSuccess { validRequest ->
            authService.register(validRequest.email, validRequest.password)
        }
    }

    fun login(loginRequest: LoginRequest): String {
        val validationResult = loginRequestValidator.validate(loginRequest)

        val token = when(validationResult) {
            is Valid -> success(validationResult.value)
            is Invalid -> failure(BadRequestResponse("bad request"))
        }.flatMap { validRequest ->
            when(val userId = authService.verify(validRequest.email, validRequest.password)) {
                null -> failure(UnauthorizedResponse("invalid credentials"))
                else -> success(userId)
            }
        }.flatMap { userId ->
            val token = jwt.signJWT(
                "$userId",
                audience = "app",
                expirationMinutes = 60,
                customClaims = null
            )
            success(token)
        }

        return token.getOrThrow()
    }
}