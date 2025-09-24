package dev.rockyj.todo.commands

import dev.rockyj.todo.config.Database
import dev.rockyj.todo.config.DatabaseTestConfiguration
import dev.rockyj.todo.repository.UserRepository
import dev.rockyj.todo.services.AuthService
import dev.rockyj.todo.services.JWTService
import dev.rockyj.todo.services.PasswordService
import io.javalin.http.BadRequestResponse
import io.javalin.http.UnauthorizedResponse
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthCommandIntegrationTest : DatabaseTestConfiguration() {

    private val db = Database()
    private val passwordService = PasswordService()
    private val jwtService = JWTService()
    private val userRepository = UserRepository(db)
    private val authService = AuthService(passwordService, userRepository)
    private val authCommand = AuthCommand(jwtService, authService)

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
    fun `should register a user successfully`() {
        val userId = authCommand.register(RegisterRequest("test@example.com", "123456", "123456"))
        assertThat(userId).isNotNull()
    }

    @Test
    fun `should not register a user when email is not right`() {
        assertThatThrownBy { authCommand.register(RegisterRequest("bademail", "123456", "123456"))  }
            .isInstanceOf(BadRequestResponse::class.java)
    }

    @Test
    fun `should not register a user when passwords are not right`() {
        assertThatThrownBy { authCommand.register(RegisterRequest("test@example.com", "1234", "1234"))  }
            .isInstanceOf(BadRequestResponse::class.java)
    }

    @Test
    fun `should not register a user when passwords do not match`() {
        assertThatThrownBy { authCommand.register(RegisterRequest("test@example.com", "123456", "1234567"))  }
            .isInstanceOf(BadRequestResponse::class.java)
    }

    @Test
    fun `should not register a user when email already exists`() {
        userRepository.create("test@example.com", "12345678")
        assertThatThrownBy { authCommand.register(RegisterRequest("test@example.com", "123456", "123456"))  }
            .isInstanceOf(BadRequestResponse::class.java)
    }

    @Test
    fun `should login a user`() {
        authCommand.register(RegisterRequest("test@example.com", "123456", "123456"))
        val token = authCommand.login(LoginRequest("test@example.com", "123456"))
        assertThat(token).isNotNull()
    }

    @Test
    fun `should not login a user with bad email`() {
        authCommand.register(RegisterRequest("test@example.com", "123456", "123456"))
        assertThatThrownBy { authCommand.login(LoginRequest("test1@example.com", "123456"))  }
            .isInstanceOf(UnauthorizedResponse::class.java)
    }

    @Test
    fun `should not login a user with bad password`() {
        authCommand.register(RegisterRequest("test@example.com", "123456", "123456"))
        assertThatThrownBy { authCommand.login(LoginRequest("test@example.com", "123456-"))  }
            .isInstanceOf(UnauthorizedResponse::class.java)
    }


}