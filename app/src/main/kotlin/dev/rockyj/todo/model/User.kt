package dev.rockyj.todo.model

data class User(
    val id: Long,
    val email: String,
    val passwordHash: String,
)
