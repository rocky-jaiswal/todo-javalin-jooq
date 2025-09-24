package dev.rockyj.todo.repository

import dev.rockyj.todo.config.Database
import dev.rockyj.todo.config.DatabaseTestConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class TodoRepositoryIntegrationTest : DatabaseTestConfiguration() {
    private val db = Database()
    private val userRepository: UserRepository = UserRepository(db)
    private val todoRepository = TodoRepository(db)

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
    fun `should save a todo`() {
        userRepository.create("test@example.com", "12345678")

        val userFromDB = userRepository.findByEmail("test@example.com")
        val userId = userFromDB?.id as Long

        val savedTodo = todoRepository.insert(userId, "todo 1", null, null, false)

        assertThat(savedTodo).isNotNull()
    }

    @Test
    fun `should delete a todo`() {
        userRepository.create("test@example.com", "12345678")

        val userFromDB = userRepository.findByEmail("test@example.com")
        val userId = userFromDB?.id as Long

        val savedTodoId = todoRepository.insert(userId, "todo 1", null, null, false)

        assertThat(savedTodoId).isNotNull()
        todoRepository.delete(userId, savedTodoId!!)

        assertThat(todoRepository.findAllForUserId(userId).size).isEqualTo(0)
    }

    @Test
    fun `should save a big todo`() {
        userRepository.create("test@example.com", "12345678")

        val userFromDB = userRepository.findByEmail("test@example.com")
        val userId = userFromDB?.id as Long
        val someDate = LocalDate.of(2025, 5, 31)

        val savedTodoId = todoRepository.insert(userId, "todo 1", "desc 111", someDate, true)

        assertThat(savedTodoId).isNotNull()

        val savedTodo = todoRepository.findById(userId, savedTodoId!!)
        assertThat(savedTodo?.dueAt).isEqualTo(someDate)
    }

    @Test
    fun `should find a todo`() {
        userRepository.create("test@example.com", "12345678")

        val userFromDB = userRepository.findByEmail("test@example.com")
        val userId = userFromDB?.id as Long
        val savedTodoId = todoRepository.insert(userId, "todo 1", null, null, false)

        assertThat(todoRepository.findById(userId, savedTodoId!!)?.title).isEqualTo("todo 1")
    }

    @Test
    fun `should find all todos`() {
        userRepository.create("test@example.com", "12345678")

        val userFromDB = userRepository.findByEmail("test@example.com")
        val userId = userFromDB?.id as Long
        todoRepository.insert(userId, "todo 1", null, null, false)
        todoRepository.insert(userId, "todo 2", null, null, false)

        assertThat(todoRepository.findAllForUserId(userId).size).isEqualTo(2)
    }
}
