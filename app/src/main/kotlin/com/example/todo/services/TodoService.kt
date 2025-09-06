package com.example.todo.services

import com.example.todo.dto.CreateTodoRequest
import com.example.todo.dto.UpdateTodoRequest
import com.example.todo.model.Todo
import com.example.todo.repository.TodoRepository
import java.time.OffsetDateTime

class TodoService(private val repo: TodoRepository) {
    fun list(userId: Long, offset: Int = 0, limit: Int = 100): List<Todo> = listOf<Todo>()

    fun get(userId: Long, id: Long) = repo.findById(userId, id)

    fun create(userId: Long, req: CreateTodoRequest): Todo? {
        val id = repo.insert(userId, req.title)
        return if (id != null) Todo(id, userId, req.title) else null
    }

    fun update(userId: Long, id: Long, req: UpdateTodoRequest): Todo? = Todo(1, 1, "scc", "", true, OffsetDateTime.now(),
        OffsetDateTime.now())

    fun delete(userId: Long, id: Long): Boolean = true
}