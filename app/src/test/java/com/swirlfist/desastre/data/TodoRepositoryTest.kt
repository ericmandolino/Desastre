package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.db.TodoDao
import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
internal class TodoRepositoryTest {
    @Mock
    lateinit var todoDao: TodoDao

    @Test
    fun observeTodos_returnTodosRetrievedViaDao() = runTest {
        // Arrange
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = TodoRepository(
            todoDao,
            ioDispatcher = testDispatcher
        )
        val todosFlow: Flow<List<Todo>> = flowOf(listOf())
        whenever(todoDao.observeAll()).thenReturn(todosFlow)

        // Act
        val result = repository.observeTodos()

        // Assert
        Assert.assertEquals(todosFlow, result)
    }

    @Test
    fun addTodo_todoInsertedViaDao() = runTest {
        // Arrange
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = TodoRepository(
            todoDao,
            ioDispatcher = testDispatcher
        )
        val todo = Todo(
            id = 0,
            title = "title",
            description = "description",
            isDone = false
        )

        // Act
        repository.addTodo(todo)

        // Assert
        verify(todoDao, times(1)).insert(todo)
    }

    @Test
    fun removeTodo_todoRemovedViaDao() = runTest {
        // Arrange
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = TodoRepository(
            todoDao,
            ioDispatcher = testDispatcher
        )
        val todoId = 1L

        // Act
        repository.removeTodo(todoId)

        // Assert
        verify(todoDao, times(1)).delete(todoId)
    }
}