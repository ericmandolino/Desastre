package com.swirlfist.desastre.data

import com.swirlfist.desastre.data.db.ReminderDao
import com.swirlfist.desastre.data.db.ReminderEntity
import com.swirlfist.desastre.data.db.TodoDao
import com.swirlfist.desastre.data.db.TodoEntity
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.check
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@RunWith(MockitoJUnitRunner::class)
internal class TodoRepositoryTest {
    @Mock
    lateinit var todoDao: TodoDao
    @Mock
    lateinit var reminderDao: ReminderDao

    @Test
    fun observeTodo_returnsFlowOfTodo() = runTest {
        // Arrange
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = TodoRepository(
            todoDao,
            reminderDao,
            ioDispatcher = testDispatcher
        )
        val todoEntity = TodoEntity(
            id = 1,
            title = "title",
            description = "description",
            isDone = false
        )
        val todoEntityFlow: Flow<TodoEntity> = flowOf(todoEntity)
        whenever(todoDao.observeTodo(1)).thenReturn(todoEntityFlow)

        // Act
        val result = repository.observeTodo(1).single()

        // Assert
        Assert.assertEquals(todoEntity.asModel(), result)
    }

    @Test
    fun observeTodos_returnFlowOfTodoList() = runTest {
        // Arrange
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = TodoRepository(
            todoDao,
            reminderDao,
            ioDispatcher = testDispatcher
        )
        val todoEntity = TodoEntity(
            id = 1,
            title = "title",
            description = "description",
            isDone = false
        )
        val todoEntityListFlow: Flow<List<TodoEntity>> = flowOf(listOf(todoEntity))
        whenever(todoDao.observeAll()).thenReturn(todoEntityListFlow)

        // Act
        val result = repository.observeTodos().single()

        // Assert
        Assert.assertEquals(listOf(todoEntity.asModel()), result)
    }

    @Test
    fun addTodo_todoAdded() = runTest {
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
        verify(todoDao, times(1)).insert(
            check { todoEntity ->
                Assert.assertEquals(todo.asEntity(), todoEntity)
            }
        )
    }

    @Test
    fun removeTodo_todoRemoved() = runTest {
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
    fun observeReminder_returnFlowOfReminder() = runTest {
        // Arrange
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = TodoRepository(
            todoDao,
            reminderDao,
            ioDispatcher = testDispatcher
        )
        val reminderEntity = ReminderEntity(
            id = 1,
            todoId = 1,
            minute = 0,
            hour = 0,
            day = 15,
            month = 10,
            year = 2040,
        )
        val reminderEntityFlow: Flow<ReminderEntity> = flowOf(reminderEntity)
        whenever(reminderDao.observeReminder(1)).thenReturn(reminderEntityFlow)

        // Act
        val result = repository.observeReminder(1).single()

        // Assert
        Assert.assertEquals(reminderEntity.asModel(), result)
    }

    @Test
    fun observeRemindersForTodo_returnFlowOfReminderList() = runTest {
        // Arrange
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = TodoRepository(
            todoDao,
            reminderDao,
            ioDispatcher = testDispatcher
        )
        val reminderEntity = ReminderEntity(
            id = 1,
            todoId = 1,
            minute = 0,
            hour = 0,
            day = 15,
            month = 10,
            year = 2040,
        )
        val reminderEntityListFlow: Flow<List<ReminderEntity>> = flowOf(listOf(reminderEntity))
        whenever(reminderDao.observeRemindersForTodo(1)).thenReturn(reminderEntityListFlow)

        // Act
        val result = repository.observeRemindersForTodo(1).single()

        // Assert
        Assert.assertEquals(listOf(reminderEntity.asModel()), result)
    }

    @Test
    fun addReminder_reminderAdded() = runTest {
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
            time = LocalDateTime.of(2040, 10, 15, 0, 0),
        )

        // Act
        repository.addOrUpdateReminder(reminder)

        // Assert
        verify(reminderDao, times(1)).insert(
            check { reminderEntity ->
                Assert.assertEquals(reminder.asEntity(), reminderEntity)
            }
        )
    }

    @Test
    fun removeReminder_reminderRemoved() = runTest {
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