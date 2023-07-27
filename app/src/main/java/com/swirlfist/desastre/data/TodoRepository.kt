package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.db.TodoDao
import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao,
    private val ioDispatcher: CoroutineDispatcher,
) : ITodoRepository {
    override fun observeTodos(): Flow<List<Todo>> {
        return todoDao.observeAll()
    }

    override suspend fun addTodo(todo: Todo) = withContext(ioDispatcher) {
        todoDao.insert(todo)
    }

    override suspend fun removeTodo(id: Long) = withContext(ioDispatcher) {
        todoDao.delete(id)
    }
}