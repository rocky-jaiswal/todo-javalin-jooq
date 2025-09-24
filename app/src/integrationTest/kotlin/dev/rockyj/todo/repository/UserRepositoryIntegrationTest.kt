package dev.rockyj.todo.repository

import dev.rockyj.todo.config.Database
import dev.rockyj.todo.config.DatabaseTestConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.DriverManager

class UserRepositoryIntegrationTest : DatabaseTestConfiguration() {
    private val userRepository: UserRepository = UserRepository(Database())

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
    fun `should work with the database`() {
        // Your integration test using the migrated database
        val connection =
            DriverManager.getConnection(
                postgres.jdbcUrl,
                postgres.username,
                postgres.password,
            )

        // Test your repository methods here
        assertTrue(postgres.isRunning)
        assertNotNull(connection)

        connection.close()
    }

    @Test
    fun `should save a user`() {
        userRepository.create("test@example.com", "12345678")

        val userFromDB = userRepository.findByEmail("test@example.com")

        assertThat(userFromDB?.email).isEqualTo("test@example.com")
    }

    @Test
    fun `should find a user`() {
        userRepository.create("test@example.com", "12345678")

        val userFromDB = userRepository.findByEmail("test@example.com")

        val id = userFromDB?.id

        assertThat(id).isNotNull()
        assertThat(userRepository.findById(id!!)?.email).isEqualTo("test@example.com")
    }

    @Test
    fun `should handle duplicate entries`() {
        val userRepository = UserRepository(Database())
        userRepository.create("test@example.com", "12345678")

        assertThatThrownBy {
            userRepository.create(
                "test@example.com",
                "123456789",
            )
        }.isInstanceOf(Exception::class.java)
    }
}
