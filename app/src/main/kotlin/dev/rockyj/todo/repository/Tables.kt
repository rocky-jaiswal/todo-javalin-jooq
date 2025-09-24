package dev.rockyj.todo.repository

import org.jooq.Field
import org.jooq.Table
import org.jooq.impl.DSL
import java.time.LocalDate

object Tables {
    object Users {
        val TABLE: Table<*> = DSL.table("users")
        val ID: Field<Long> = DSL.field("id", Long::class.java)
        val EMAIL: Field<String> = DSL.field("email", String::class.java)
        val PASSWORD_HASH: Field<String> = DSL.field("password_hash", String::class.java)
    }

    object Todos {
        val TABLE: Table<*> = DSL.table("todos")
        val ID: Field<Long> = DSL.field("id", Long::class.java)
        val USER_ID: Field<Long> = DSL.field("user_id", Long::class.java)
        val TITLE: Field<String> = DSL.field("title", String::class.java)
        val DESCRIPTION: Field<String?> = DSL.field("description", String::class.java)
        val DUE_AT: Field<LocalDate?> = DSL.field("due_at", LocalDate::class.java)
        val COMPLETED: Field<Boolean?> = DSL.field("completed", Boolean::class.java)
    }
}
