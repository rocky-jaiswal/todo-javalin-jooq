package com.example.todo.repository

import com.example.todo.config.Database

class UserRepository(private val db: Database) {
    val dsl = db.dsl()

    fun findByEmail(email: String) = dsl
        .select(
            Tables.Users.ID,
            Tables.Users.EMAIL,
            Tables.Users.PASSWORD_HASH
        )
        .from(Tables.Users.TABLE)
        .where(Tables.Users.EMAIL.eq(email))
        .fetchOne()

    fun findById(id: Long) = dsl.selectFrom(Tables.Users.TABLE)
        .where(Tables.Users.ID.eq(id))
        .fetchOne()

    fun create(email: String, passwordHash: String): Long? {
        return dsl
            .insertInto(Tables.Users.TABLE)
            .columns(Tables.Users.EMAIL, Tables.Users.PASSWORD_HASH)
            .values(email, passwordHash)
            .returningResult(Tables.Users.ID)
            .fetchOne()?.get(Tables.Users.ID)
    }
}