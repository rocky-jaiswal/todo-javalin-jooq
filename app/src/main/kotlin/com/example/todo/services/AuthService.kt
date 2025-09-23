package com.example.todo.services

import com.example.todo.repository.Tables
import com.example.todo.repository.UserRepository

class AuthService(private val passwordService: PasswordService, private val userRepository: UserRepository) {
    fun userExists(email: String): Long? {
        val record = userRepository.findByEmail(email)
        return record?.get(Tables.Users.ID)
    }

    fun register(email: String, password: String): Long {
        val hash = passwordService.hashPassword(password)
        return userRepository.create(email.lowercase(), hash) ?: throw RuntimeException("could not create user")
    }

    fun verify(email: String, password: String): Long? {
        val rec = userRepository.findByEmail(email.lowercase()) ?: return null
        val verified = passwordService.verifyPassword(password, rec[Tables.Users.PASSWORD_HASH] as String)
        return if (verified) (rec.get(Tables.Users.ID) as Long) else null
    }
}