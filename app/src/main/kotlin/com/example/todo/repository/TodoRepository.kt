package com.example.todo.repository

import org.jooq.DSLContext

class TodoRepository(private val dsl: DSLContext) {
    fun findById(userId: Long, id: Long) = dsl
        .selectFrom(Tables.Todos.TABLE)
        .where(Tables.Todos.ID.eq(id).and(Tables.Todos.USER_ID.eq(userId)))
        .fetchOneInto(Map::class.java)

    fun insert(userId: Long, title: String): Long? = dsl
        .insertInto(Tables.Todos.TABLE)
        .columns(Tables.Todos.USER_ID, Tables.Todos.TITLE, Tables.Todos.COMPLETED)
        .values(userId, title, false)
        .returningResult(Tables.Todos.ID)
        .fetchOne()!![Tables.Todos.ID]

    fun update(userId: Long, id: Long, title: String, completed: Boolean) = dsl
        .update(Tables.Todos.TABLE)
        .set(Tables.Todos.TITLE, title)
        .set(Tables.Todos.COMPLETED, completed)
        .where(Tables.Todos.ID.eq(id).and(Tables.Todos.USER_ID.eq(userId)))
        .execute()

    fun delete(userId: Long, id: Long) = dsl
        .deleteFrom(Tables.Todos.TABLE)
        .where(Tables.Todos.ID.eq(id).and(Tables.Todos.USER_ID.eq(userId)))
        .execute()
}