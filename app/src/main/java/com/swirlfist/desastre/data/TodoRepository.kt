package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class TodoRepository : ITodoRepository {
    private val todos = MutableList(
        size = 100,
        init = { index -> Todo(
            id = index.toLong(),
            title = "title $index",
            description = "description $index",
            isDone = index % 3 == 0,
        ) }
    )

    override fun observeTodos(): Flow<List<Todo>> {
        return flowOf(todos)
    }
}