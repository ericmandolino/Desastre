package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.flow.Flow

interface ITodoRepository {
    fun observeTodo(todoId: Long): Flow<Todo?>
    fun observeTodos(): Flow<List<Todo>>
    suspend fun addTodo(todo: Todo): Long
    suspend fun removeTodo(id: Long)
    fun observeReminder(id: Long): Flow<Reminder?>
    fun observeRemindersForTodo(todoId: Long): Flow<List<Reminder>>
    suspend fun addReminder(reminder: Reminder): Long
    suspend fun removeReminder(id: Long)
}