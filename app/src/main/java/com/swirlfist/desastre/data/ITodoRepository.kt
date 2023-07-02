package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.flow.Flow

interface ITodoRepository {
    fun observeTodos(): Flow<List<Todo>>
    fun addTodo(todo: Todo)
    fun removeTodo(id: Long)
}