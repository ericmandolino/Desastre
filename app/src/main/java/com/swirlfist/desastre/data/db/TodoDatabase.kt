package com.swirlfist.desastre.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [TodoEntity::class, ReminderEntity::class],
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun TodoDao(): TodoDao

    abstract fun ReminderDao(): ReminderDao
}