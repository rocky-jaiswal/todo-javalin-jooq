package dev.rockyj.todo.config

import dev.rockyj.todo.entities.User
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

object HibernateConfig {
    val sessionFactory: SessionFactory by lazy {
        val cfg = Configuration().configure()

        // Inject secrets at runtime (from env or config file)
        cfg.setProperty("hibernate.connection.url", Secrets.JDBC_URL())
        cfg.setProperty("hibernate.connection.username", Secrets.JDBC_USER)
        cfg.setProperty("hibernate.connection.password", Secrets.JDBC_PASSWORD)

        cfg.addAnnotatedClass(User::class.java)

        val serviceRegistry = StandardServiceRegistryBuilder()
            .applySettings(cfg.properties)
            .build()

        cfg.buildSessionFactory(serviceRegistry)
    }
}
