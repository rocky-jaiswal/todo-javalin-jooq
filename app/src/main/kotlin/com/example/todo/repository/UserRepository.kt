package com.example.todo.repository

import org.jooq.DSLContext

class UserRepository(private val dsl: DSLContext) {
    fun findByEmail(email: String) = dsl
        .select(
            Tables.Users.ID,
            Tables.Users.EMAIL,
            Tables.Users.PASSWORD
        )
        .from(Tables.Users.TABLE)
        .where(Tables.Users.EMAIL.eq(email))
        .fetchOne()

    fun findById(id: Long) = dsl.selectFrom(Tables.Users.TABLE)
        .where(Tables.Users.ID.eq(id))
        .fetchOneInto(Map::class.java)

    fun create(email: String, passwordHash: String) = dsl
        .insertInto(Tables.Users.TABLE)
        .columns(Tables.Users.EMAIL, Tables.Users.PASSWORD)
        .values(email, passwordHash)
        .returningResult(Tables.Users.ID)
        .fetchOne()!![Tables.Users.ID]
}