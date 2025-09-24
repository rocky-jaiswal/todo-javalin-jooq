package dev.rockyj.todo.controllers

import dev.rockyj.todo.commands.CreateTodoRequest
import dev.rockyj.todo.commands.TodoCommand
import io.javalin.http.Context
import io.javalin.http.bodyValidator

class TodoController(
    private val todoCommand: TodoCommand,
) {
    fun list(ctx: Context) {
        val userId = ctx.attribute<String>("userId")!!
        ctx.status(200).json(todoCommand.list(userId.toLong()))
    }

    fun get(ctx: Context) {
        val userId = ctx.attribute<String>("userId")!!
        val id = ctx.pathParamAsClass<Long>("id", Long::class.java).check({ true }, "not null").get()
        val item = todoCommand.get(userId.toLong(), id) ?: ctx.status(404).json(mapOf("error" to "Not Found"))
        ctx.status(200).json(item)
    }

    fun create(ctx: Context) {
        val userId = ctx.attribute<String>("userId")!!
        val created = todoCommand.create(userId.toLong(), ctx.bodyValidator<CreateTodoRequest>().get())
        ctx.status(201).json(created)
    }

//    fun update(ctx: Context) {
//        val userId = ctx.attribute<String>("userId")!!
//        val id = ctx.pathParamAsClass<Long>("id", Long::class.java).check({ it > 0 }, ">0").get()
//        val body = ctx.bodyValidator<UpdateTodoRequest>()
//            .check({ it.title == null || it.title.isNotBlank() }, "title must not be blank when present")
//            .check({ it.title == null || it.title.length <= 200 }, "title <= 200 chars when present")
//            .get()
//        val updated = todoService.update(userId.toLong(), id, body) ?: ctx.status(404).json(mapOf("error" to "Not Found"))
//        ctx.json(updated)
//    }

    fun delete(ctx: Context) {
        val userId = ctx.attribute<String>("userId")!!
        val id = ctx.pathParamAsClass<Long>("id", Long::class.java).check({ true }, "not null").get()
        if (todoCommand.delete(userId.toLong(), id) == 1) {
            ctx.status(204)
        } else {
            ctx.status(500)
        }
    }
}
