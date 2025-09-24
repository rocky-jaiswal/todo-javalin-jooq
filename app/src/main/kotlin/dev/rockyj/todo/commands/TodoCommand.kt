package dev.rockyj.todo.commands

import dev.rockyj.todo.model.Todo
import dev.rockyj.todo.services.TodoService
import dev.rockyj.todo.utils.failure
import dev.rockyj.todo.utils.flatMap
import dev.rockyj.todo.utils.getOrThrow
import dev.rockyj.todo.utils.success
import io.javalin.http.BadRequestResponse
import io.konform.validation.Invalid
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.konform.validation.constraints.notBlank
import java.time.LocalDate

data class CreateTodoRequest(val title: String, val description: String? = null, val dueAt: LocalDate?, val completed: Boolean?)

// data class UpdateTodoRequest(val title: String?, val description: String?, val dueAt: LocalDate?, val completed: Boolean?)

val createTodoRequestValidator = Validation<CreateTodoRequest> {
    CreateTodoRequest::title {
        notBlank()
    }
}

class TodoCommand(private val todoService: TodoService) {

    fun list(userId: Long): List<Todo> {
        return todoService.list(userId)
    }

    fun get(userId: Long, id: Long): Todo? {
        return todoService.get(userId, id)
    }

    fun delete(userId: Long, id: Long): Int {
        return todoService.delete(userId, id)
    }

    fun create(userId: Long, createTodoRequest: CreateTodoRequest): Todo {
        val validationResult = createTodoRequestValidator.validate(createTodoRequest)

        val createdTodo = when(validationResult) {
            is Valid -> success(validationResult.value)
            is Invalid -> failure(BadRequestResponse("bad request"))
        }.flatMap { validRequest ->
            val created = todoService.create(
                Todo(
                    null,
                    userId,
                    validRequest.title,
                    validRequest.description,
                    validRequest.dueAt,
                    validRequest.completed ?: false
                )
            )
            success(created)
        }.getOrThrow()

        return createdTodo
    }
}