package com.swirlfist.desastre.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = TodoEntity::class,
            parentColumns = ["id"],
            childColumns = ["todoId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
        )
    ]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) val todoId: Long,
    @ColumnInfo val minute: Int,
    @ColumnInfo val hour: Int,
    @ColumnInfo val day: Int,
    @ColumnInfo val month: Int,
    @ColumnInfo val year: Int,
)
