package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.db.ReminderDao
import com.swirlfist.desastre.data.db.TodoDao
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao,
    private val reminderDao: ReminderDao,
    private val ioDispatcher: CoroutineDispatcher,
) : ITodoRepository {
    override fun observeTodo(todoId: Long): Flow<Todo?> {
        return todoDao.observeTodo(todoId)
    }

    override fun observeTodos(): Flow<List<Todo>> {
        return todoDao.observeAll()
    }

    override suspend fun addTodo(todo: Todo): Long = withContext(ioDispatcher) {
        todoDao.insert(todo)
    }

    override suspend fun removeTodo(todoId: Long) = withContext(ioDispatcher) {
        todoDao.delete(todoId)
    }

    override fun observeReminder(reminderId: Long): Flow<Reminder?> {
        return reminderDao.observeReminder(reminderId)
    }
    override fun observeRemindersForTodo(todoId: Long): Flow<List<Reminder>> {
        return reminderDao.observeRemindersForTodo(todoId)
    }
    override suspend fun addReminder(reminder: Reminder): Long = withContext(ioDispatcher) {
        reminderDao.insert(reminder)
    }
    override suspend fun removeReminder(reminderId: Long) = withContext(ioDispatcher) {
        reminderDao.delete(reminderId)
    }
}