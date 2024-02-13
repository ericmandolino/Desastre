package com.swirlfist.desastre.data.db

import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import java.time.LocalDateTime

fun TodoEntity.asModel() = Todo(
    id = this.id,
    title = this.title,
    description = this.description,
    isDone = this.isDone,
)

fun Todo.asEntity() = TodoEntity(
    id = this.id,
    title = this.title,
    description = this.description,
    isDone = this.isDone,
)

fun ReminderEntity.asModel() = Reminder(
    id = this.id,
    todoId = this.todoId,
    time = LocalDateTime.of(
        this.year,
        this.month,
        this.day,
        this.hour,
        this.minute
    )
)

fun Reminder.asEntity() = ReminderEntity(
    id = this.id,
    todoId = this.todoId,
    minute = this.time.minute,
    hour = this.time.hour,
    day = this.time.dayOfMonth,
    month = this.time.monthValue,
    year = this.time.year,
)