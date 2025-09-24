package dev.rockyj.todo.middlewares

import dev.rockyj.todo.services.JWTService
import io.javalin.http.Context
import io.javalin.http.UnauthorizedResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AuthMiddleware(private val jwt: JWTService) {

    private val logger: Logger = LoggerFactory.getLogger(AuthMiddleware::class.java)

    fun authenticate(ctx: Context) {
        try {
            if (ctx.path().startsWith("/api/public") ||
                ctx.path().startsWith("/api/auth")) {
                return
            }

            val auth = ctx.header("Authorization")
            if (auth == null || !auth.startsWith("Bearer ")) {
                throw RuntimeException("Bad authentication header")
            }

            val jwtClaimSet = jwt.verifyJWT(auth.removePrefix("Bearer ").trim())

            ctx.attribute("userId", jwtClaimSet.subject)
        } catch(ex: Exception) {
            logger.error("Error in auth middleware", ex)
            throw UnauthorizedResponse("unauthorized access")
        }
    }

}