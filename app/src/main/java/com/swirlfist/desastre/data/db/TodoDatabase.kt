package com.swirlfist.desastre.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo

@Database(
    version = 1,
    entities = [Todo::class, Reminder::class],
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun TodoDao(): TodoDao

    abstract fun ReminderDao(): ReminderDao
}