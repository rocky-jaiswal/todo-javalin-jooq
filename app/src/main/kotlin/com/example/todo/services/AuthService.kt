package com.example.todo.services

import com.example.todo.repository.Tables
import com.example.todo.repository.UserRepository

class AuthService(private val passwordService: PasswordService, private val userRepository: UserRepository) {
    fun register(email: String, password: String) {
        val hash = passwordService.hashPassword(password)
        userRepository.create(email.lowercase(), hash)
    }

    fun verify(email: String, password: String): Long? {
        val rec = userRepository.findByEmail(email.lowercase()) ?: return null
        val verified = passwordService.verifyPassword(password, rec[Tables.Users.PASSWORD_HASH] as String)
        return if (verified) (rec.get(Tables.Users.ID) as Long) else null
    }
}