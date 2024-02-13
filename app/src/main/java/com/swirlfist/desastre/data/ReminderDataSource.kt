package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderDataSource {
    suspend fun insert(reminder: Reminder): Long

    suspend fun delete(reminderId: Long)

    fun observeReminder(reminderId: Long): Flow<Reminder?>

    fun observeRemindersForTodo(todoId: Long): Flow<List<Reminder>>
}