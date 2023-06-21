package com.swirlfist.desastre.ui.view

import com.swirlfist.desastre.data.model.Todo

class PreviewUtil {
    companion object {
        fun mockTodo(
            id: Long? = null,
            title: String? = null,
            description: String? = null,
            isDone: Boolean = false,
        ): Todo {
            val id = id?: 1L
            return Todo(
                id = id,
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
    }
}