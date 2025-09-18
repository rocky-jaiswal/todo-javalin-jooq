package com.example.todo.dto

import io.konform.validation.Validation
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.notBlank
import io.konform.validation.constraints.pattern

data class CreateTodoRequest(val title: String, val description: String? = null)

data class UpdateTodoRequest(val title: String? = null, val description: String? = null, val completed: Boolean? = null)

data class RegisterRequest(val email: String, val password: String)

data class LoginRequest(val email: String, val password: String)

// Konform validators
val userRequestValidator = Validation<RegisterRequest> {
    RegisterRequest::email {
        minLength(1) hint "email cannot be blank"
        pattern(Regex("(.*)@(.*)"))
    }
    RegisterRequest::password {
        minLength(6) hint "password must be 6 characters"
    }
}

// Konform validators
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