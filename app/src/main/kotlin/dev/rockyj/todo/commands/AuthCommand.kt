package dev.rockyj.todo.commands

import dev.rockyj.todo.services.AuthService
import dev.rockyj.todo.services.JWTService
import dev.rockyj.todo.utils.*
import io.javalin.http.BadRequestResponse
import io.javalin.http.UnauthorizedResponse
import io.konform.validation.Invalid
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.notBlank
import io.konform.validation.constraints.pattern

data class RegisterRequest(
    val email: String,
    val password: String,
    val passwordConfirmation: String,
)

data class LoginRequest(
    val email: String,
    val password: String,
)

val userRequestValidator =
    Validation {
        RegisterRequest::email {
            minLength(1) hint "email cannot be blank"
            pattern(Regex("(.*)@(.*)"))
        }
        RegisterRequest::password {
            minLength(6) hint "password must be 6 characters"
        }
        RegisterRequest::passwordConfirmation {
            minLength(6) hint "password must be 6 characters"
        }
    }

val loginRequestValidator =
    Validation {
        LoginRequest::email {
            notBlank()
            pattern(Regex("(.*)@(.*)"))
        }
        LoginRequest::password {
            notBlank()
            minLength(6) hint "password must be 6 characters"
        }
    }

class AuthCommand(
    private val jwt: JWTService,
    private val authService: AuthService,
) {
    fun register(registerRequest: RegisterRequest): Long? {
        val validationResult = userRequestValidator.validate(registerRequest)

        val userId =
            when (validationResult) {
                is Valid -> success(validationResult.value)
                is Invalid -> failure(BadRequestResponse("bad request"))
            }.flatMap { validRequest ->
                when (validRequest.password == validRequest.passwordConfirmation) {
                    true -> success(validRequest)
                    false -> failure(BadRequestResponse("bad request"))
                }
            }.flatMap { validRequest ->
                val existingUserId = authService.userExists(validRequest.email)
                if (existingUserId == null) {
                    success(validRequest)
                } else {
                    failure(BadRequestResponse("bad request"))
                }
            }.flatMap { validRequest ->
                buildResult { tryOperation { authService.register(validRequest.email, validRequest.password) } }
            }

        return userId.getOrThrow()
    }

    fun login(loginRequest: LoginRequest): String {
        val validationResult = loginRequestValidator.validate(loginRequest)

        val token =
            when (validationResult) {
                is Valid -> success(validationResult.value)
                is Invalid -> failure(BadRequestResponse("bad request"))
            }.flatMap { validRequest ->
                when (val userId = authService.verify(validRequest.email, validRequest.password)) {
                    null -> failure(UnauthorizedResponse("invalid credentials"))
                    else -> success(userId)
                }
            }.flatMap { userId ->
                val token =
                    jwt.signJWT(
                        "$userId",
                        audience = "app",
                        expirationMinutes = 60,
                        customClaims = null,
                    )
                success(token)
            }

        return token.getOrThrow()
    }
}
