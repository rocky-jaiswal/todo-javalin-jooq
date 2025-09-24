package dev.rockyj.todo.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StructuredLogging {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StructuredLogging::class.java)
    }

    // Utility class for common logging patterns
    object LoggingUtils {
        fun logApiCall(
            extLogger: Logger?,
            endpoint: String?,
            method: String?,
            duration: Long?,
            statusCode: Int?,
        ) {
            val lggr = extLogger ?: logger

            lggr
                .atInfo()
                .addKeyValue("type", "api_call")
                .addKeyValue("endpoint", endpoint)
                .addKeyValue("method", method)
                // .addKeyValue("duration_ms", duration)
                // .addKeyValue("status_code", statusCode)
                .log("API call")
        }

        fun logUserAction(
            extLogger: Logger?,
            userId: String?,
            action: String?,
            details: Any,
        ) {
            val lggr = extLogger ?: logger

            lggr
                .atInfo()
                .addKeyValue("type", "user_action")
                .addKeyValue("user_id", userId)
                .addKeyValue("action", action)
                .addKeyValue("details", details.toString())
                .log("User action performed")
        }
    }
}
