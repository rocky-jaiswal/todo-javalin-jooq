package dev.rockyj.todo.model

import java.time.LocalDate

data class Todo(
    val id: Long?,
    val userId: Long,
    val title: String,
    val description: String?,
    val dueAt: LocalDate?,
    val completed: Boolean?,
    val createdAt: java.time.OffsetDateTime? = null,
    val updatedAt: java.time.OffsetDateTime? = null,
)
