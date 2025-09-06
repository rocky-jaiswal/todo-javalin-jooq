package com.example.todo.controllers

import com.example.todo.dto.CreateTodoRequest
import com.example.todo.dto.UpdateTodoRequest
import com.example.todo.services.TodoService
import io.javalin.http.Context
import io.javalin.http.bodyValidator
import io.javalin.validation.ValidationException

class TodoController(private val todoService: TodoService) {

    fun list(ctx: Context) {
        val userId = ctx.attribute<String>("userId")!!
        val offset = ctx.queryParamAsClass<Int>("offset", Int::class.java).allowNullable().check({ it == null || it >= 0 }, "offset>=0").get() ?: 0
        val limit = ctx.queryParamAsClass<Int>("limit", Int::class.java).allowNullable().check({ it == null || (it in 1..500) }, "1<=limit<=500").get() ?: 100
        ctx.json(todoService.list(userId.toLong(), offset, limit))
    }

    fun get(ctx: Context) {
        val userId = ctx.attribute<String>("userId")!!
        val id = ctx.pathParamAsClass<Long>("id", Long::class.java).check({ it > 0 }, ">0").get()
        val item = todoService.get(userId.toLong(), id) ?: ctx.status(404).json(mapOf("error" to "Not Found"))
        ctx.json(item)
    }

    fun create(ctx: Context) {
        val userId = ctx.attribute<String>("userId")!!
        val body = ctx.bodyValidator<CreateTodoRequest>()
            .check({ it.title.isNotBlank() }, "title must not be blank")
            .check({ it.title.length <= 200 }, "title <= 200 chars")
            .get()
        val created = todoService.create(userId.toLong(), body)
        ctx.status(201).json(created ?: emptyMap<String, String>())
    }

    fun update(ctx: Context) {
        val userId = ctx.attribute<String>("userId")!!
        val id = ctx.pathParamAsClass<Long>("id", Long::class.java).check({ it > 0 }, ">0").get()
        val body = ctx.bodyValidator<UpdateTodoRequest>()
            .check({ it.title == null || it.title.isNotBlank() }, "title must not be blank when present")
            .check({ it.title == null || it.title.length <= 200 }, "title <= 200 chars when present")
            .get()
        val updated = todoService.update(userId.toLong(), id, body) ?: ctx.status(404).json(mapOf("error" to "Not Found"))
        ctx.json(updated)
    }

    fun delete(ctx: Context) {
        val userId = ctx.attribute<String>("userId")!!
        val id = ctx.pathParamAsClass<Long>("id", Long::class.java).check({ it > 0 }, ">0").get()
        val ok = todoService.delete(userId.toLong(), id)
        if (!ok) ctx.status(404).json(mapOf("error" to "Not Found"))
        ctx.status(204)
    }

    fun handleValidationErrors(e: ValidationException, ctx: Context) { ctx.status(400).json(mapOf("error" to e.errors)) }
}