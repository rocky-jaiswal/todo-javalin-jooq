package dev.rockyj.todo.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import javax.sql.DataSource

class Database {
    val dataSource: DataSource by lazy {
        val config =
            HikariConfig().apply {
                jdbcUrl = Secrets.JDBC_URL
                this.username = Secrets.JDBC_USER
                this.password = Secrets.JDBC_PASSWORD
                this.maximumPoolSize = 10
                this.minimumIdle = 1000
                driverClassName = "org.postgresql.Driver"

                // Connection pool settings to prevent connection leaks
                connectionTimeout = 30000 // 30 seconds
                idleTimeout = 600000 // 10 minutes
                maxLifetime = 1800000 // 30 minutes
                leakDetectionThreshold = 60000 // 60 seconds - will log potential leaks

                // Validate connections
                connectionTestQuery = "SELECT 1"
                validationTimeout = 5000
            }
        HikariDataSource(config)
    }

    // JOOQ Configuration that properly manages connections
    private val jooqConfiguration: Configuration by lazy {
        DefaultConfiguration()
            .set(dataSource)
            .set(SQLDialect.POSTGRES)
    }

    // fun connection(): Connection = dataSource.connection

    fun dsl(): DSLContext = DSL.using(jooqConfiguration)

    // Sample method to execute queries with transaction support
    fun <T> withTransaction(block: (DSLContext) -> T): T =
        dataSource.connection.use { connection ->
            connection.autoCommit = false
            val ctx = DSL.using(jooqConfiguration)
            try {
                val result = block(ctx)
                connection.commit()
                result
            } catch (e: Exception) {
                connection.rollback()
                throw e
            }
        }
}
