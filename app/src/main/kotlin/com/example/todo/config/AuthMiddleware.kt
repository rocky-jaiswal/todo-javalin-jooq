package com.example.todo.config

import com.example.todo.services.JWTService
import io.javalin.http.Context

class AuthMiddleware(private val jwt: JWTService) {

    fun authenticate(ctx: Context): Boolean {
        try {
            val auth = ctx.header("Authorization") ?: return false
            if (!auth.startsWith("Bearer ")) return false
            val token = auth.removePrefix("Bearer ").trim()
            val jwtClaimSet = jwt.verifyJWT(token)
            ctx.attribute("userId", jwtClaimSet.subject)
        } catch(_ex: Exception) {
            // TODO: log exception
            return false
        }
        return true
    }

}
