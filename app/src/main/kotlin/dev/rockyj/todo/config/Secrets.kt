package dev.rockyj.todo.config

import io.github.cdimascio.dotenv.dotenv

val env = System.getenv("APP_ENV") ?: "local"

val dotenv =
    dotenv {
        filename = ".env.$env"
    }

object Secrets {
    val APP_PORT = dotenv.get("APP_PORT").toInt()
    val JDBC_USER = dotenv.get("JDBC_USER") ?: throw RuntimeException("cannot read jdbc user")
    val JDBC_PASSWORD = dotenv.get("JDBC_PASSWORD") ?: throw RuntimeException("cannot read jdbc password")
    val KEY_PASSWORD = dotenv.get("KEY_PASSWORD") ?: throw RuntimeException("cannot read jwt key secret")
    val PRIVATE_KEY_FILE = dotenv.get("PRIVATE_KEY_FILE") ?: throw RuntimeException("cannot read jwt private key")
    val PUBLIC_KEY_FILE = dotenv.get("PUBLIC_KEY_FILE") ?: throw RuntimeException("cannot read jwt public")
    val JDBC_URL: () -> String = {
        if (env ==
            "test"
        ) {
            System.getProperty("JDBC_URL") ?: throw RuntimeException("cannot read jdbc url in test env")
        } else {
            dotenv.get("JDBC_URL")
                ?: throw RuntimeException("cannot read jdbc url")
        }
    }
}
