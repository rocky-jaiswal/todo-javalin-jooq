package com.example.todo.services

import com.example.todo.model.Todo
import com.example.todo.repository.TodoRepository

class TodoService(private val todoRepository: TodoRepository) {
    fun list(userId: Long): List<Todo> {
        return todoRepository.findAllForUserId(userId)
    }

    fun get(userId: Long, id: Long): Todo? {
        val todo = todoRepository.findById(userId, id)
        return todo
    }

    fun create(newTodo: Todo): Todo {
        val newId = todoRepository.insert(newTodo.userId, newTodo.title, newTodo.description, newTodo.dueAt, newTodo.completed)
        return Todo(newId, newTodo.userId, newTodo.title, newTodo.description, newTodo.dueAt, newTodo.completed)
    }

//    fun update(todo: Todo): Todo? {
//
//    }

    fun delete(userId: Long, id: Long) = todoRepository.delete(userId, id)
}