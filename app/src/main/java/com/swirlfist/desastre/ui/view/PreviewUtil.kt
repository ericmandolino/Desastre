package com.swirlfist.desastre.ui.view

import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import java.time.LocalDate

object PreviewUtil {
    fun mockTodo(
        id: Long? = null,
        title: String? = null,
        description: String? = null,
        isDone: Boolean = false,
    ): Todo {
        val todoId = id?: 1L
        return Todo(
            id = todoId,
            title = title?: "Title $id",
            description = description?: "Description $id",
            isDone = isDone,
        )
    }

    fun mockTodos(
        size: Int,
    ): List<Todo> {
        val todos = mutableListOf<Todo>()
        for (i in 0..size) {
            todos.add(
                mockTodo(
                    id = i.toLong(),
                )
            )
        }
        return todos.toList()
    }

    fun mockReminder() = Reminder(
        id = 0,
        todoId = 0,
        minute = 15,
        hour = 13,
        day = 25,
        month = 10,
        year = 2048,
    )

    fun mockTodayReminder(): Reminder {
        val today = LocalDate.now()
        return Reminder(
            id = 0,
            todoId = 0,
            minute = 15,
            hour = 13,
            day = today.dayOfMonth,
            month = today.monthValue,
            year = today.year,
        )
    }

    fun mockTomorrowReminder(): Reminder {
        val tomorrow = LocalDate.now().plusDays(1)
        return Reminder(
            id = 0,
            todoId = 0,
            minute = 15,
            hour = 13,
            day = tomorrow.dayOfMonth,
            month = tomorrow.monthValue,
            year = tomorrow.year,
        )
    }

    fun mockYesterdayReminder(): Reminder {
        val yesterday = LocalDate.now().minusDays(1)
        return Reminder(
            id = 0,
            todoId = 0,
            minute = 15,
            hour = 13,
            day = yesterday.dayOfMonth,
            month = yesterday.monthValue,
            year = yesterday.year,
        )
    }

    fun mockReminders(size: Int): List<Reminder> {
        val reminders = mutableListOf<Reminder>()
        repeat(size) {
            reminders.add(mockReminder())
        }
        return reminders
    }
}