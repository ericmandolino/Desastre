package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDataSource: TodoDataSource,
    private val reminderDataSource: ReminderDataSource,
) : ITodoRepository {
    override fun observeTodo(todoId: Long): Flow<Todo?> = todoDataSource.observeTodo(todoId)

    override fun observeTodos() = todoDataSource.observeAll()

    override suspend fun addOrUpdateTodo(todo: Todo): Long = todoDataSource.insert(todo)

    override suspend fun removeTodo(todoId: Long) = todoDataSource.delete(todoId)

    override fun observeReminder(reminderId: Long) = reminderDataSource.observeReminder(reminderId)

    override fun observeRemindersForTodo(todoId: Long) = reminderDataSource.observeRemindersForTodo(todoId)

    override suspend fun addOrUpdateReminder(reminder: Reminder) = reminderDataSource.insert(reminder)

    override suspend fun removeReminder(reminderId: Long) = reminderDataSource.delete(reminderId)
}