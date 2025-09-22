package com.example.todo.middlewares

import io.javalin.http.Context
import io.javalin.http.ExceptionHandler
import io.javalin.http.Handler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.Map
import java.util.UUID

object RequestLogging {
    private val logger: Logger = LoggerFactory.getLogger(RequestLogging::class.java)
    private const val REQUEST_START_TIME = "request_start_time"
    private const val REQUEST_ID = "requestId"
    private const val USER_ID = "userId"
    private const val SESSION_ID = "sessionId"

    // Sensitive headers that should not be logged
    private val SENSITIVE_HEADERS = mutableSetOf<String>(
        "authorization", "cookie", "x-api-key", "x-auth-token",
        "password", "secret", "token"
    )

    // Headers to exclude from logging (too verbose or not useful)
    private val EXCLUDED_HEADERS = mutableSetOf<String?>(
        "user-agent", "accept-encoding", "accept-language",
        "cache-control", "connection", "upgrade-insecure-requests"
    )

    /**
     * Before handler to log incoming requests and set up MDC context
     */
    fun beforeHandler(): Handler {
        return Handler { ctx: Context? ->
            val startTime = System.currentTimeMillis()
            ctx!!.attribute(REQUEST_START_TIME, startTime)

            // Generate request ID
            val requestId = ctx.header("x-request-correlation-id") ?: generateRequestId()
            ctx.attribute(REQUEST_ID, requestId)

            // Set up MDC context
            setupMDC(ctx, requestId)

            // Log incoming request
            logIncomingRequest(ctx, startTime)
        }
    }

    /**
     * After handler to log responses and cleanup MDC
     */
    fun afterHandler(): Handler {
        return Handler { ctx: Context? ->
            try {
                val startTime = ctx!!.attribute<Long?>(REQUEST_START_TIME)
                if (startTime != null) {
                    val duration = System.currentTimeMillis() - startTime
                    logOutgoingResponse(ctx, duration)
                }
            } finally {
                // Always clean up MDC
                MDC.clear()
            }
        }
    }

    fun logRequestError(ctx: Context, exception: Exception, duration: Long) {
        logger.atError()
            .addKeyValue("event_type", "http_error")
            .addKeyValue("http_method", ctx.method().name)
            .addKeyValue("http_path", ctx.path())
            .addKeyValue("http_status", ctx.status())
            .addKeyValue("error_class", exception.javaClass.getSimpleName())
            .addKeyValue("error_message", exception.message)
            .addKeyValue("duration_ms", duration)
            .addKeyValue("error_timestamp", formatTimestamp(System.currentTimeMillis()))
            .setCause(exception)
            .log("HTTP request failed with exception")
    }

    private fun setupMDC(ctx: Context, requestId: String?) {
        MDC.put(REQUEST_ID, requestId)

        // Extract user information from headers/tokens (customize as needed)
        val userId = extractUserId(ctx)
        val sessionId = extractSessionId(ctx)

        if (userId != null) {
            MDC.put(USER_ID, userId)
        }

        if (sessionId != null) {
            MDC.put(SESSION_ID, sessionId)
        }

        // Add IP address
        MDC.put("clientIp", getClientIp(ctx))
        MDC.put("userAgent", ctx.userAgent())
    }

    private fun logIncomingRequest(ctx: Context, startTime: Long) {
        val queryParams = ctx.queryParamMap()

        logger.atInfo()
            .addKeyValue("event_type", "http_request")
            .addKeyValue("http_method", ctx.method().name)
            .addKeyValue("http_path", ctx.path())
            .addKeyValue("http_url", ctx.url())
            .addKeyValue("query_params", queryParams)
            .addKeyValue("headers", ctx.headerMap())
            .addKeyValue("content_type", ctx.contentType())
            .addKeyValue("content_length", ctx.contentLength())
            .addKeyValue("client_ip", getClientIp(ctx))
            .addKeyValue("user_agent", ctx.userAgent())
            .addKeyValue("request_timestamp", formatTimestamp(startTime))
            .log("Incoming HTTP request")
    }

    private fun logOutgoingResponse(ctx: Context, duration: Long) {
        logger.atInfo()
            .addKeyValue("event_type", "http_response")
            .addKeyValue("http_method", ctx.method().name)
            .addKeyValue("http_path", ctx.path())
            .addKeyValue("http_status", ctx.status())
            .addKeyValue("response_headers", ctx.res().headerNames)
            .addKeyValue("content_type", ctx.res().getContentType())
            .addKeyValue("duration_ms", duration)
            .addKeyValue("response_timestamp", formatTimestamp(System.currentTimeMillis()))
            .log("HTTP response sent")
    }

//    private fun filterHeaders(headers: MutableMap<kotlin.String?, kotlin.String?>): MutableMap<kotlin.String?, kotlin.String?> {
//        return headers.entries()
//            .filter { entry: MutableMap.MutableEntry<kotlin.String?, kotlin.String?>? ->
//                !SENSITIVE_HEADERS.contains(
//                    entry!!.key!!.lowercase(Locale.getDefault())
//                )
//            }
//            .filter { entry: MutableMap.MutableEntry<kotlin.String?, kotlin.String?>? ->
//                !EXCLUDED_HEADERS.contains(
//                    entry!!.key!!.lowercase(Locale.getDefault())
//                )
//            }
//            .collect(
//                Collectors.toMap(
//                    Function { Map.Entry.key },
//                    Function { entry: MutableMap.MutableEntry<kotlin.String?, kotlin.String?>? ->
//                        RequestLoggingHandler.maskSensitiveValue(
//                            entry!!.key!!,
//                            entry.value
//                        )
//                    }
//                ))
//    }

    private fun maskSensitiveValue(headerName: String, value: String?): String? {
        val lowerName = headerName.lowercase(Locale.getDefault())
        if (lowerName.contains("auth") || lowerName.contains("token") ||
            lowerName.contains("key") || lowerName.contains("secret")
        ) {
            return "***MASKED***"
        }
        return value
    }

    // TODO: Fix me
    private fun extractUserId(ctx: Context): String? {
        // Example: Extract from custom header
        val userIdHeader = ctx.header("X-User-Id")
        if (userIdHeader != null) {
            return userIdHeader
        }


        // Example: Extract from JWT token (simplified)
        val authHeader = ctx.header("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // In real implementation, decode JWT and extract user ID
            return "extracted-from-jwt"
        }

        return null
    }

    // TODO: Fix me
    private fun extractSessionId(ctx: Context): String? {
        // Example: Extract from cookie
        val sessionCookie = ctx.cookie("JSESSIONID")
        if (sessionCookie != null) {
            return sessionCookie
        }


        // Example: Extract from custom header
        return ctx.header("X-Session-Id")
    }

    // TODO: Fix me
    private fun getClientIp(ctx: Context): String {
        // Check for forwarded headers first
        val xForwardedFor = ctx.header("X-Forwarded-For")
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Take the first IP in the chain
            return xForwardedFor.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[0].trim { it <= ' ' }
        }

        val xRealIp = ctx.header("X-Real-IP")
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp
        }


        // Fallback to remote address
        return ctx.ip()
    }

    private fun generateRequestId(): String {
        return "req-" + UUID.randomUUID().toString()
    }

    private fun formatTimestamp(epochMilli: Long): String {
        return Instant.ofEpochMilli(epochMilli)
            .atOffset(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT)
    }
}