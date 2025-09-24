package dev.rockyj.todo.services

import dev.rockyj.todo.repository.UserRepository

class AuthService(
    private val passwordService: PasswordService,
    private val userRepository: UserRepository,
) {
    fun userExists(email: String): Long? {
        val record = userRepository.findByEmail(email)
        return record?.id
    }

    fun register(
        email: String,
        password: String,
    ): Long {
        val hash = passwordService.hashPassword(password)
        return userRepository.create(email.lowercase(), hash) ?: throw RuntimeException("could not create user")
    }

    fun verify(
        email: String,
        password: String,
    ): Long? {
        val rec = userRepository.findByEmail(email.lowercase()) ?: return null
        val verified = passwordService.verifyPassword(password, rec.passwordHash)
        return if (verified) (rec.id) else null
    }
}
