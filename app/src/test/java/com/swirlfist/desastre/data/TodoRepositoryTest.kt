package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.db.ReminderDao
import com.swirlfist.desastre.data.db.TodoDao
import com.swirlfist.desastre.data.model.Reminder
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
    @Mock
    lateinit var reminderDao: ReminderDao

    @Test
    fun observeTodo_returnFlowOfTodoRetrievedViaDao() = runTest {
        // Arrange
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = TodoRepository(
            todoDao,
            reminderDao,
            ioDispatcher = testDispatcher
        )
        val todo = Todo(
            id = 1,
            title = "title",
            description = "description",
            isDone = false
        )
        val todoFlow: Flow<Todo> = flowOf(todo)
        whenever(todoDao.observeTodo(1)).thenReturn(todoFlow)

        // Act
        val result = repository.observeTodo(1)

        // Assert
        Assert.assertEquals(todoFlow, result)
    }

    @Test
    fun observeTodos_returnFlowOfTodoListRetrievedViaDao() = runTest {
        // Arrange
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = TodoRepository(
            todoDao,
            reminderDao,
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
            reminderDao,
            ioDispatcher = testDispatcher
        )
        val todo = Todo(
            id = 0,
            title = "title",
            description = "description",
            isDone = false
        )

        // Act
        repository.addOrUpdateTodo(todo)

        // Assert
        verify(todoDao, times(1)).insert(todo)
    }

    @Test
    fun removeTodo_todoRemovedViaDao() = runTest {
        // Arrange
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = TodoRepository(
            todoDao,
            reminderDao,
            ioDispatcher = testDispatcher
        )
        val todoId = 1L

        // Act
        repository.removeTodo(todoId)

        // Assert
        verify(todoDao, times(1)).delete(todoId)
    }

    @Test
    fun observeRemindersForTodo_returnFlowOfReminderListRetrievedViaDao() = runTest {
        // Arrange
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = TodoRepository(
            todoDao,
            reminderDao,
            ioDispatcher = testDispatcher
        )
        val reminderListFlow: Flow<List<Reminder>> = flowOf(listOf())
        whenever(reminderDao.observeRemindersForTodo(1)).thenReturn(reminderListFlow)

        // Act
        val result = repository.observeRemindersForTodo(1)

        // Assert
        Assert.assertEquals(reminderListFlow, result)
    }

    @Test
    fun observeReminder_returnFlowOfReminderRetrievedViaDao() = runTest {
        // Arrange
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = TodoRepository(
            todoDao,
            reminderDao,
            ioDispatcher = testDispatcher
        )
        val reminder = Reminder(
            id = 1,
            todoId = 1,
            minute = 0,
            hour = 0,
            day = 15,
            month = 10,
            year = 2040,
        )
        val reminderFlow: Flow<Reminder> = flowOf(reminder)
        whenever(reminderDao.observeReminder(1)).thenReturn(reminderFlow)

        // Act
        val result = repository.observeReminder(1)

        // Assert
        Assert.assertEquals(reminderFlow, result)
    }

    @Test
    fun addReminder_reminderInsertedViaDao() = runTest {
        // Arrange
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = TodoRepository(
            todoDao,
            reminderDao,
            ioDispatcher = testDispatcher
        )
        val reminder = Reminder(
            id = 1,
            todoId = 1,
            minute = 0,
            hour = 0,
            day = 15,
            month = 10,
            year = 2040,
        )

        // Act
        repository.addOrUpdateReminder(reminder)

        // Assert
        verify(reminderDao, times(1)).insert(reminder)
    }

    @Test
    fun removeReminder_reminderRemovedViaDao() = runTest {
        // Arrange
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = TodoRepository(
            todoDao,
            reminderDao,
            ioDispatcher = testDispatcher
        )
        val reminderId = 1L

        // Act
        repository.removeReminder(reminderId)

        // Assert
        verify(reminderDao, times(1)).delete(reminderId)
    }
}