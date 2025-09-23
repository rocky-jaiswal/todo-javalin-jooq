package com.example.todo.repository

import com.example.todo.config.Database
import com.example.todo.model.Todo
import java.time.LocalDate

class TodoRepository(private val db: Database) {
    val dsl = db.dsl()

    fun findAllForUserId(userId: Long) = dsl
        .selectFrom(Tables.Todos.TABLE)
        .where(Tables.Todos.USER_ID.eq(userId))
        .fetchInto<Todo>(Todo::class.java)

    fun findById(userId: Long, id: Long) = dsl
        .selectFrom(Tables.Todos.TABLE)
        .where(Tables.Todos.ID.eq(id).and(Tables.Todos.USER_ID.eq(userId)))
        .fetchOneInto(Todo::class.java)

    fun insert(userId: Long, title: String, description: String?, dueAt: LocalDate?, completed: Boolean?): Long? = dsl
        .insertInto(Tables.Todos.TABLE)
        .columns(Tables.Todos.USER_ID, Tables.Todos.TITLE, Tables.Todos.DESCRIPTION, Tables.Todos.DUE_AT,Tables.Todos.COMPLETED)
        .values(userId, title, description, dueAt, completed)
        .returningResult(Tables.Todos.ID)
        .fetchOne()?.get(Tables.Todos.ID)

    fun update(userId: Long, id: Long, title: String, description: String?, dueAt: LocalDate, completed: Boolean?) = dsl
        .update(Tables.Todos.TABLE)
        .set(Tables.Todos.TITLE, title)
        .set(Tables.Todos.DESCRIPTION, description)
        .set(Tables.Todos.DUE_AT, dueAt)
        .set(Tables.Todos.COMPLETED, completed)
        .where(Tables.Todos.ID.eq(id).and(Tables.Todos.USER_ID.eq(userId)))
        .execute()

    fun delete(userId: Long, id: Long) = dsl
        .deleteFrom(Tables.Todos.TABLE)
        .where(Tables.Todos.ID.eq(id).and(Tables.Todos.USER_ID.eq(userId)))
        .execute()
}