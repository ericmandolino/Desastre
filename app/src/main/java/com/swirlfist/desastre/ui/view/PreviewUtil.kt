package com.swirlfist.desastre.ui.view

import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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
        time = LocalDateTime.of(2048, 10, 25, 13, 15),
    )

    fun mockTodayReminder(): Reminder {
        val today = LocalDate.now()
        return Reminder(
            id = 0,
            todoId = 0,
            time = LocalDateTime.of(today, LocalTime.of(13, 15)),
        )
    }

    fun mockTomorrowReminder(): Reminder {
        val tomorrow = LocalDate.now().plusDays(1)
        return Reminder(
            id = 0,
            todoId = 0,
            time = LocalDateTime.of(tomorrow, LocalTime.of(13, 15)),
        )
    }

    fun mockYesterdayReminder(): Reminder {
        val yesterday = LocalDate.now().minusDays(1)
        return Reminder(
            id = 0,
            todoId = 0,
            time = LocalDateTime.of(yesterday, LocalTime.of(13, 15)),
        )
    }

    fun mockReminders(size: Int): List<Reminder> {
        val reminders = mutableListOf<Reminder>()
        repeat(size) {
            reminders.add(mockReminder())
        }
        return reminders
    }

    fun mockTitle() = "Aliquid facilis aperiam itaque et cumque sed totam est."

    fun mockDescription() = "Aliquid facilis aperiam itaque et cumque sed totam est. Esse soluta modi perspiciatis. Placeat quis cum et enim. Quia reiciendis reprehenderit atque. Ea quaerat id nihil repudiandae. Et tenetur consectetur ad ipsa quia.\n\n" +
            "Aliquid facilis aperiam itaque et cumque sed totam est. Esse soluta modi perspiciatis. Placeat quis cum et enim. Quia reiciendis reprehenderit atque. Ea quaerat id nihil repudiandae. Et tenetur consectetur ad ipsa quia.\n\n" +
            "Aliquid facilis aperiam itaque et cumque sed totam est. Esse soluta modi perspiciatis. Placeat quis cum et enim. Quia reiciendis reprehenderit atque. Ea quaerat id nihil repudiandae. Et tenetur consectetur ad ipsa quia.\n\n" +
            "Aliquid facilis aperiam itaque et cumque sed totam est. Esse soluta modi perspiciatis. Placeat quis cum et enim. Quia reiciendis reprehenderit atque. Ea quaerat id nihil repudiandae. Et tenetur consectetur ad ipsa quia.\n\n" +
            "Aliquid facilis aperiam itaque et cumque sed totam est. Esse soluta modi perspiciatis. Placeat quis cum et enim. Quia reiciendis reprehenderit atque. Ea quaerat id nihil repudiandae. Et tenetur consectetur ad ipsa quia."
}