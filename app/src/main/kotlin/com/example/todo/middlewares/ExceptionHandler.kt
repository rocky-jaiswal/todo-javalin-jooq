package com.example.todo.middlewares

import io.javalin.http.Context
import io.javalin.http.ExceptionHandler
import io.javalin.http.HttpResponseException
import java.time.Instant
import java.util.Map

object ExceptionHandler {

    fun exceptionHandler(): io.javalin.http.ExceptionHandler<Exception> {
        return ExceptionHandler { exception: Exception, ctx: Context? ->
            val startTime = ctx!!.attribute<Long?>("request_start_time")
            val duration = if (startTime != null) System.currentTimeMillis() - startTime else 0

            RequestLogging.logRequestError(ctx, exception, duration)

            when (exception) {
                is HttpResponseException -> ctx.status(exception.status).json(
                    Map.of(
                        "error", exception.message ?: "unknown exception",
                        "timestamp", Instant.now().toString()
                    )
                )
                else -> ctx.status(500).json(
                    Map.of(
                        "error", "Internal Server Error",
                        "timestamp", Instant.now().toString()
                    )
                )
            }


        }
    }
}