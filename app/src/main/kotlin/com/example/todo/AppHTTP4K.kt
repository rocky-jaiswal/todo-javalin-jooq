package com.example.todo

//import com.fasterxml.jackson.databind.ObjectMapper
//import org.http4k.core.*
//import org.http4k.filter.ServerFilters
//import org.http4k.format.Jackson
//import org.http4k.routing.RoutingHttpHandler
//import org.http4k.routing.bind
//import org.http4k.routing.path
//import org.http4k.routing.routes
//import org.http4k.server.Jetty
//import org.http4k.server.asServer
//
//data class Author(
//    val id: Int,
//    val name: String
//)
//
//data class Book(
//    val id: Int,
//    val title: String,
//    val authorId: Int
//)
//
//// Helpers for JSON
//inline fun <reified T : Any> Request.toObject(): T? =
//    try { Jackson.asA(this.bodyString()) } catch (_: Exception) { null }
//
//fun Response.withJson(obj: Any, status: Status = Status.OK): Response =
//    Response(status).body(ObjectMapper().writeValueAsString(obj))
//        .header("Content-Type", "application/json")
//
//// Custom filter to enforce presence of a header
//fun requireHeaderFilter(headerName: String): Filter = Filter { next ->
//    { req: Request ->
//        val headerValue = req.header(headerName)
//        if (headerValue.isNullOrBlank()) {
//            Response(Status.UNAUTHORIZED).body("Missing required header: $headerName")
//        } else {
//            next(req)
//        }
//    }
//}
//
//object Database {
//    val books = mutableListOf(
//        Book(1, "The Hobbit", 1),
//        Book(2, "1984", 2)
//    )
//}
//
//object HealthRoute {
//    val check: HttpHandler = {
//        Response(Status.OK)
//    }
//}
//
//object BookHandlers {
//    val getAll: HttpHandler = {
//        Response(Status.OK).withJson(Database.books)
//    }
//
//    val getById: HttpHandler = { req ->
//        val id = req.path("id")?.toIntOrNull()
//        val book = Database.books.find { it.id == id }
//        if (book != null) {
//            Response(Status.OK).withJson(book)
//        } else {
//            Response(Status.NOT_FOUND)
//        }
//    }
//
//    val create: HttpHandler = { req ->
//        val newBook = req.toObject<Book>()
//        Response(Status.CREATED).withJson(Database.books)
//    }
//}
//
//object HealthRoutes {
//    fun routes(): RoutingHttpHandler = routes(
//        "/health" bind Method.GET to HealthRoute.check
//    )
//
//    operator fun invoke(): RoutingHttpHandler = routes()
//}
//
//object BookRoutes {
//    fun routes(): RoutingHttpHandler = routes(
//        "/books" bind Method.GET to BookHandlers.getAll,
//        "/books/{id}" bind Method.GET to BookHandlers.getById,
//        "/books" bind Method.POST to BookHandlers.create
//    )
//
//    operator fun invoke(): RoutingHttpHandler = routes()
//}
//
//fun main() {
//    val server = ServerFilters.CatchAll()
//        .then(requireHeaderFilter("X-API-KEY"))
//        .then(routes(BookRoutes(), HealthRoutes()))
//        .asServer(Jetty(port = 8080))
//
//    server.start()
//    println("🚀 Server running with virtual threads at http://localhost:8080 (requires X-API-KEY)")
//}