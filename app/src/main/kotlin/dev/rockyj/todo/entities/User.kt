package dev.rockyj.todo.entities


import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: Long? = null,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false, name = "password_hash")
    val passwordHash: String
)
