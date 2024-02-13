package com.swirlfist.desastre.data.db

import com.swirlfist.desastre.data.TodoDataSource
import com.swirlfist.desastre.data.asEntity
import com.swirlfist.desastre.data.asModel
import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TodoDataSourceRoom @Inject constructor(
    private val todoDao: TodoDao,
    private val ioDispatcher: CoroutineDispatcher,
) : TodoDataSource {
    override suspend fun insert(todo: Todo): Long = withContext(ioDispatcher) {
        todoDao.insert(todo.asEntity())
    }

    override suspend fun delete(todoId: Long) = withContext(ioDispatcher) {
        todoDao.delete(todoId)
    }

    override fun observeAll(): Flow<List<Todo>> = todoDao.observeAll().map { todoEntityList ->
        todoEntityList.map { todoEntity -> todoEntity.asModel() }
    }

    override fun observeTodo(todoId: Long): Flow<Todo?> =
        todoDao.observeTodo(todoId).map { todoEntity ->
            todoEntity?.asModel()
        }
}