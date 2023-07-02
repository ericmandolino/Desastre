package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TodoRepository : ITodoRepository {
    private var todos = MutableList(
        size = 0,
        init = { index -> Todo(
            id = index.toLong(),
            title = "title $index",
            description = "description $index",
            isDone = index % 3 == 0,
        ) },
    )
    private var trySendDataBlocking: (data: List<Todo>) -> Unit = { }
    private var closeChannel: () -> Unit = { }
    private var todosFlow: Flow<List<Todo>> = callbackFlow {
        send(todos)
        trySendDataBlocking = { data -> trySendBlocking(data) }
        closeChannel = { close() }
        awaitClose {
            trySendDataBlocking = {}
            closeChannel = {}
        }
    }

    override fun observeTodos(): Flow<List<Todo>> {
        return todosFlow
    }

    override fun addTodo(todo: Todo) {
        todos.add(todo)
        trySendDataBlocking(todos)
    }

    override fun removeTodo(id: Long) {
        todos.removeIf { todo ->
            todo.id == id
        }
        trySendDataBlocking(todos)
    }
}