package dev.rockyj.todo.commands

import dev.rockyj.todo.config.Database
import dev.rockyj.todo.config.DatabaseTestConfiguration
import dev.rockyj.todo.repository.TodoRepository
import dev.rockyj.todo.repository.UserRepository
import dev.rockyj.todo.services.TodoService
import io.javalin.http.BadRequestResponse
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TodoCommandIntegrationTest : DatabaseTestConfiguration() {
    private val db = Database()
    private val userRepository = UserRepository(db)
    private val todoRepository = TodoRepository(db)
    private val todoService = TodoService(todoRepository)
    private val todoCommand = TodoCommand(todoService)

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUpDatabase() {
            setupDatabase()
        }
    }

    @BeforeEach
    fun cleanBetweenTests() {
        cleanDatabase()
    }

    @Test
    fun `should create a todo successfully`() {
        val userId = userRepository.create("test@example.com", "12345678")
        val todo = todoCommand.create(userId!!, CreateTodoRequest("title1", null, null, false))

        val todoFromDB = todoRepository.findById(userId, todo.id!!)
        assertThat(todoFromDB).isNotNull()
    }

    @Test
    fun `should not create a todo if the title is not right`() {
        val userId = userRepository.create("test@example.com", "12345678")
        assertThatThrownBy { todoCommand.create(userId!!, CreateTodoRequest("", null, null, false)) }
            .isInstanceOf(BadRequestResponse::class.java)
    }

    @Test
    fun `should list all todos for a user`() {
        val userId = userRepository.create("test@example.com", "12345678")
        val todo1 = todoCommand.create(userId!!, CreateTodoRequest("title1", null, null, false))
        val todo2 = todoCommand.create(userId!!, CreateTodoRequest("title2", null, null, false))

        val todosFromDB = todoRepository.findAllForUserId(userId)
        assertThat(todosFromDB.size).isEqualTo(2)
    }

    @Test
    fun `should delete a todo`() {
        val userId = userRepository.create("test@example.com", "12345678")
        val todo = todoCommand.create(userId!!, CreateTodoRequest("title1", null, null, false))

        assertThat(todoCommand.delete(userId, todo.id!!)).isEqualTo(1)
    }

    @Test
    fun `should update a todo`() {
        // TODO
    }
}
