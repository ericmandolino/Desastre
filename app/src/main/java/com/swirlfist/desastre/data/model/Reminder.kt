package com.swirlfist.desastre.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = Todo::class,
            parentColumns = ["id"],
            childColumns = ["todoId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
        )
    ]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(index = true) val todoId: Long,
    @ColumnInfo val minute: Int,
    @ColumnInfo val hour: Int,
    @ColumnInfo val day: Int,
    @ColumnInfo val month: Int,
    @ColumnInfo val year: Int,
)
