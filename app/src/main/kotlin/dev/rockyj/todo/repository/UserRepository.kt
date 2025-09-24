package dev.rockyj.todo.repository

import dev.rockyj.todo.config.Database
import dev.rockyj.todo.model.User

class UserRepository(
    private val db: Database,
) {
    val dsl = db.dsl()

    fun findByEmail(email: String) =
        dsl
            .select(
                Tables.Users.ID,
                Tables.Users.EMAIL,
                Tables.Users.PASSWORD_HASH,
            ).from(Tables.Users.TABLE)
            .where(Tables.Users.EMAIL.eq(email))
            .fetchOneInto(User::class.java)

    fun findById(id: Long) =
        dsl
            .selectFrom(Tables.Users.TABLE)
            .where(Tables.Users.ID.eq(id))
            .fetchOneInto(User::class.java)

    fun create(
        email: String,
        passwordHash: String,
    ): Long? =
        dsl
            .insertInto(Tables.Users.TABLE)
            .columns(Tables.Users.EMAIL, Tables.Users.PASSWORD_HASH)
            .values(email, passwordHash)
            .returningResult(Tables.Users.ID)
            .fetchOne()
            ?.get(Tables.Users.ID)
}
