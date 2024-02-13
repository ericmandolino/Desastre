package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoDataSource {
    suspend fun addOrUpdate(todo: Todo): Long

    suspend fun delete(todoId: Long)

    fun observeAll(): Flow<List<Todo>>

    fun observeTodo(todoId: Long): Flow<Todo?>
}