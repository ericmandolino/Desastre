package com.swirlfist.desastre.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id=:reminderId")
    suspend fun delete(reminderId: Long)

    @Query("SELECT * from reminders WHERE id=:reminderId")
    fun observeReminder(reminderId: Long): Flow<ReminderEntity?>

    @Query("SELECT * from reminders WHERE todoId=:todoId")
    fun observeRemindersForTodo(todoId: Long): Flow<List<ReminderEntity>>
}