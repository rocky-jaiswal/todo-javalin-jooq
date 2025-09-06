package com.example.todo.dto

data class CreateTodoRequest(val title: String, val description: String? = null)

data class UpdateTodoRequest(val title: String? = null, val description: String? = null, val completed: Boolean? = null)

data class RegisterRequest(val email: String, val password: String)

data class LoginRequest(val email: String, val password: String)

