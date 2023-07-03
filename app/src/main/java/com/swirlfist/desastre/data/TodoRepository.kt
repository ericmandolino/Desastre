package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.db.TodoDao
import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) : ITodoRepository {
    override fun observeTodos(): Flow<List<Todo>> {
        return todoDao.observeAll()
    }

    override fun addTodo(
        todo: Todo,
        coroutineScope: CoroutineScope,
    ) {
        coroutineScope.launch(
            context = Dispatchers.IO
        ) {
            todoDao.insert(todo)
        }
    }

    override fun removeTodo(
        id: Long,
        coroutineScope: CoroutineScope,
    ) {
        coroutineScope.launch(
            context = Dispatchers.IO
        ) {
            todoDao.delete(id)
        }
    }
}