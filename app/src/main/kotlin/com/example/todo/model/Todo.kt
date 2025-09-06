package com.example.todo.model

data class Todo(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String? = null,
    val completed: Boolean? = null,
    val createdAt: java.time.OffsetDateTime? = null,
    val updatedAt: java.time.OffsetDateTime? = null
)
