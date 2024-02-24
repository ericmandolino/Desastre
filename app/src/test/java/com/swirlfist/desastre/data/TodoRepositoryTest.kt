package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@RunWith(MockitoJUnitRunner::class)
internal class TodoRepositoryTest {
    @Mock
    lateinit var todoDataSource: TodoDataSource
    @Mock
    lateinit var reminderDataSource: ReminderDataSource

    @Test
    fun observeTodo_returnsFlowOfTodo() = runTest {
        // Arrange
        val repository = TodoRepositoryImpl(
            todoDataSource,
            reminderDataSource,
        )
        val todo = Todo(
            id = 1,
            title = "title",
            description = "description",
            isDone = false
        )
        val todoFlow: Flow<Todo> = flowOf(todo)
        whenever(todoDataSource.observeTodo(1)).thenReturn(todoFlow)

        // Act
        val result = repository.observeTodo(1).single()

        // Assert
        Assert.assertEquals(todo, result)
    }

    @Test
    fun observeTodos_returnFlowOfTodoList() = runTest {
        // Arrange
        val repository = TodoRepositoryImpl(
            todoDataSource,
            reminderDataSource,
        )
        val todo = Todo(
            id = 1,
            title = "title",
            description = "description",
            isDone = false
        )
        val todoListFlow: Flow<List<Todo>> = flowOf(listOf(todo))
        whenever(todoDataSource.observeAll()).thenReturn(todoListFlow)

        // Act
        val result = repository.observeTodos().single()

        // Assert
        Assert.assertEquals(listOf(todo), result)
    }

    @Test
    fun addTodo_todoAdded() = runTest {
        // Arrange
        val repository = TodoRepositoryImpl(
            todoDataSource,
            reminderDataSource,
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
        verify(todoDataSource, times(1)).addOrUpdate(todo)
    }

    @Test
    fun removeTodo_todoRemoved() = runTest {
        // Arrange
        val repository = TodoRepositoryImpl(
            todoDataSource,
            reminderDataSource,
        )
        val todoId = 1L

        // Act
        repository.removeTodo(todoId)

        // Assert
        verify(todoDataSource, times(1)).delete(todoId)
    }

    @Test
    fun observeReminder_returnFlowOfReminder() = runTest {
        // Arrange
        val repository = TodoRepositoryImpl(
            todoDataSource,
            reminderDataSource,
        )
        val reminder = Reminder(
            id = 1,
            todoId = 1,
            time = LocalDateTime.of(2040, 10, 15, 0, 0),
        )
        val reminderFlow: Flow<Reminder> = flowOf(reminder)
        whenever(reminderDataSource.observeReminder(1)).thenReturn(reminderFlow)

        // Act
        val result = repository.observeReminder(1).single()

        // Assert
        Assert.assertEquals(reminder, result)
    }

    @Test
    fun observeRemindersForTodo_returnFlowOfReminderList() = runTest {
        // Arrange
        val repository = TodoRepositoryImpl(
            todoDataSource,
            reminderDataSource,
        )
        val reminder = Reminder(
            id = 1,
            todoId = 1,
            time = LocalDateTime.of(2040, 10, 15, 0, 0),
        )
        val reminderListFlow: Flow<List<Reminder>> = flowOf(listOf(reminder))
        whenever(reminderDataSource.observeRemindersForTodo(1)).thenReturn(reminderListFlow)

        // Act
        val result = repository.observeRemindersForTodo(1).single()

        // Assert
        Assert.assertEquals(listOf(reminder), result)
    }

    @Test
    fun addReminder_reminderAdded() = runTest {
        // Arrange
        val repository = TodoRepositoryImpl(
            todoDataSource,
            reminderDataSource,
        )
        val reminder = Reminder(
            id = 1,
            todoId = 1,
            time = LocalDateTime.of(2040, 10, 15, 0, 0),
        )

        // Act
        repository.addOrUpdateReminder(reminder)

        // Assert
        verify(reminderDataSource, times(1)).insert(reminder)
    }

    @Test
    fun removeReminder_reminderRemoved() = runTest {
        // Arrange
        val repository = TodoRepositoryImpl(
            todoDataSource,
            reminderDataSource,
        )
        val reminderId = 1L

        // Act
        repository.removeReminder(reminderId)

        // Assert
        verify(reminderDataSource, times(1)).delete(reminderId)
    }
}