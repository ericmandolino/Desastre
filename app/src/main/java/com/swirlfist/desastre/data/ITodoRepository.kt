package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface ITodoRepository {
    fun observeTodos(): Flow<List<Todo>>
    fun addTodo(
        todo: Todo,
        coroutineScope: CoroutineScope,
    )
    fun removeTodo(
        id: Long,
        coroutineScope: CoroutineScope,
    )
}