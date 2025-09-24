package dev.rockyj.todo.services

import dev.rockyj.todo.model.User
import dev.rockyj.todo.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.jooq.Record3
import org.junit.jupiter.api.Test

class AuthServiceTest {

    @Test
    fun user_registration() {
        val userRepository = mockk<UserRepository>()
        val passwordService = mockk<PasswordService>()

        every { userRepository.create(any(String::class), any(String::class)) } returns 1L
        every { passwordService.hashPassword(any(String::class)) } returns ""

        val authService = AuthService(passwordService, userRepository)
        assertThat(authService.register("test@example.com", "123456")).isNotNull()
    }

    @Test
    fun user_login() {
        val userRepository = mockk<UserRepository>()
        val passwordService = mockk<PasswordService>()

        val record = User(2L, "email", "pass")

        every { userRepository.findByEmail(any(String::class)) } returns record

        every { passwordService.verifyPassword(any(String::class), any(String::class)) } returns true

        val authService = AuthService(passwordService, userRepository)
        assertThat(authService.verify("test@example.com", "123456")).isEqualTo(2L)
    }
}