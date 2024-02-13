package com.swirlfist.desastre.data.db

import com.swirlfist.desastre.data.ReminderDataSource
import com.swirlfist.desastre.data.asEntity
import com.swirlfist.desastre.data.asModel
import com.swirlfist.desastre.data.model.Reminder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReminderDataSourceRoom @Inject constructor(
    private val reminderDao: ReminderDao,
    private val ioDispatcher: CoroutineDispatcher,
) : ReminderDataSource {
    override suspend fun insert(reminder: Reminder): Long = withContext(ioDispatcher) {
        reminderDao.insert(reminder.asEntity())
    }

    override suspend fun delete(reminderId: Long) = withContext(ioDispatcher) {
        reminderDao.delete(reminderId)
    }

    override fun observeReminder(reminderId: Long) =
        reminderDao.observeReminder(reminderId).map { reminderEntity -> reminderEntity?.asModel() }

    override fun observeRemindersForTodo(todoId: Long) =
        reminderDao.observeRemindersForTodo(todoId).map { reminderEntityList ->
            reminderEntityList.map { reminderEntity -> reminderEntity.asModel() }
        }
}