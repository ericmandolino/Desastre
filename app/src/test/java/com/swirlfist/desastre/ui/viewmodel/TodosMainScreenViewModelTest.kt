package com.swirlfist.desastre.ui.viewmodel

import com.swirlfist.desastre.data.CoroutineDispatcherProvider
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.domain.AddOrUpdateTodoUseCase
import com.swirlfist.desastre.domain.ObserveRemindersForTodoUseCase
import com.swirlfist.desastre.domain.ObserveTodoListUseCase
import com.swirlfist.desastre.domain.RemoveTodoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class TodosMainScreenViewModelTest {
    @Mock
    lateinit var coroutineDispatcherProvider: CoroutineDispatcherProvider

    @Mock
    lateinit var observeTodoListUseCase: ObserveTodoListUseCase

    @Mock
    lateinit var observeRemindersForTodoUseCase: ObserveRemindersForTodoUseCase

    @Mock
    lateinit var addOrUpdateTodoUseCase: AddOrUpdateTodoUseCase

    @Mock
    lateinit var removeTodoUseCase: RemoveTodoUseCase

    private lateinit var viewModel: TodosMainScreenViewModel

    @Before
    fun setUp() {
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        whenever(coroutineDispatcherProvider.getMain()).thenReturn(testDispatcher)
        viewModel = TodosMainScreenViewModel(
            coroutineDispatcherProvider,
            observeTodoListUseCase,
            observeRemindersForTodoUseCase,
            addOrUpdateTodoUseCase,
            removeTodoUseCase,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `observing the TODO list returns a flow of list of TODOs`() {
        // Arrange
        val todoListFlow: Flow<List<Todo>> = flowOf(listOf())
        whenever(observeTodoListUseCase()).thenReturn(todoListFlow)

        // Act
        val result = viewModel.observeTodoList()

        // Assert
        Assert.assertEquals(todoListFlow, result)
    }

    @Test
    fun `starting the process to add a TODO creates an addition state`() {
        // Arrange

        // Act
        viewModel.startAddTodo()
        val todoAdditionState = viewModel.todoAdditionState.value!!

        // Assert
        Assert.assertEquals("", todoAdditionState.titleInputState.titleText)
        Assert.assertEquals("", todoAdditionState.descriptionInputState.descriptionText)
        Assert.assertEquals(TodoTitleValidationResult.SUCCESS, todoAdditionState.titleInputState.titleValidation)
        Assert.assertFalse(todoAdditionState.addReminder)
    }

    @Test
    fun `cancelling a TODO creation clears the addition state`() {
        // Arrange
        viewModel.startAddTodo()
        Assert.assertNotNull(viewModel.todoAdditionState.value)

        // Act
        viewModel.cancelAddTodo()

        // Assert
        Assert.assertNull(viewModel.todoAdditionState.value)
    }

    @Test
    fun `changes in the title while creating a todo are reflected in the state`() {
        // Arrange
        viewModel.startAddTodo()
        var todoAdditionState = viewModel.todoAdditionState.value!!
        val newTitle = "new title"

        // Act
        todoAdditionState.titleInputState.onTitleValueChanged(newTitle)
        todoAdditionState = viewModel.todoAdditionState.value!!

        // Assert
        Assert.assertEquals(newTitle, todoAdditionState.titleInputState.titleText)
    }

    @Test
    fun `while creating a todo changes in the title that make it too long are truncated in the state`() {
        // Arrange
        viewModel.startAddTodo()
        var todoAdditionState = viewModel.todoAdditionState.value!!
        val newTitle = mockString(55)

        // Act
        todoAdditionState.titleInputState.onTitleValueChanged(newTitle)
        todoAdditionState = viewModel.todoAdditionState.value!!

        // Assert
        Assert.assertEquals(newTitle.substring(0, 50), todoAdditionState.titleInputState.titleText)
    }

    @Test
    fun `changes in the description while creating a todo are reflected in the state`() {
        // Arrange
        viewModel.startAddTodo()
        var todoAdditionState = viewModel.todoAdditionState.value!!
        val newDescription = "new description"

        // Act
        todoAdditionState.descriptionInputState.onDescriptionValueChanged(newDescription)
        todoAdditionState = viewModel.todoAdditionState.value!!

        // Assert
        Assert.assertEquals(newDescription, todoAdditionState.descriptionInputState.descriptionText)
    }

    @Test
    fun `while creating a todo changes in the description that make it too long are truncated in the state`() {
        // Arrange
        viewModel.startAddTodo()
        var todoAdditionState = viewModel.todoAdditionState.value!!
        val newDescription = mockString(2010)

        // Act
        todoAdditionState.descriptionInputState.onDescriptionValueChanged(newDescription)
        todoAdditionState = viewModel.todoAdditionState.value!!

        // Assert
        Assert.assertEquals(newDescription.substring(0, 2000), todoAdditionState.descriptionInputState.descriptionText)
    }

    @Test
    fun `changes in the check to add a reminder while creating a todo are reflected in the state`() {
        // Arrange
        viewModel.startAddTodo()
        var todoAdditionState = viewModel.todoAdditionState.value!!

        // Act
        todoAdditionState.onAddReminderChanged(false)
        todoAdditionState.onAddReminderChanged(true)
        todoAdditionState = viewModel.todoAdditionState.value!!

        // Assert
        Assert.assertTrue(todoAdditionState.addReminder)
    }

    @Test
    fun `attempting to complete a TODO creation with an empty title updates the state with the error`() {
        // Arrange
        viewModel.startAddTodo()
        var todoAdditionState = viewModel.todoAdditionState.value!!

        // Act
        todoAdditionState.titleInputState.onTitleValueChanged("")
        viewModel.completeAddTodo {}
        todoAdditionState = viewModel.todoAdditionState.value!!

        // Assert
        Assert.assertEquals(TodoTitleValidationResult.TITLE_CANNOT_BE_EMPTY, todoAdditionState.titleInputState.titleValidation)
    }

    @Test
    fun `successfully completing a TODO creation invokes the use case to add the new TODO`() = runTest {
        // Arrange
        val title = "title"
        val description = "description"
        val addReminder = true
        viewModel.startAddTodo()
        val todoAdditionState = viewModel.todoAdditionState.value!!
        todoAdditionState.titleInputState.onTitleValueChanged(title)
        todoAdditionState.descriptionInputState.onDescriptionValueChanged(description)
        todoAdditionState.onAddReminderChanged(addReminder)
        whenever(addOrUpdateTodoUseCase(org.mockito.kotlin.any())).thenReturn(1L)

        // Act
        viewModel.completeAddTodo {}
        advanceUntilIdle()

        // Assert
        verify(addOrUpdateTodoUseCase, times(1))(org.mockito.kotlin.check { todo ->
            Assert.assertEquals(0, todo.id)
            Assert.assertEquals(title, todo.title)
            Assert.assertEquals(description, todo.description)
            Assert.assertFalse(todo.isDone)
        })
    }

    @Test
    fun `successfully completing a TODO creation clears the addition state`() = runTest {
        // Arrange
        val title = "title"
        viewModel.startAddTodo()
        val todoAdditionState = viewModel.todoAdditionState.value!!
        todoAdditionState.titleInputState.onTitleValueChanged(title)
        whenever(addOrUpdateTodoUseCase(org.mockito.kotlin.any())).thenReturn(1L)

        // Act
        viewModel.completeAddTodo {}
        advanceUntilIdle()

        // Assert
        Assert.assertNull(viewModel.todoAdditionState.value)
    }

    @Test
    fun `when successfully completing a TODO with the check to add a reminder we navigate to do so`() = runTest {
        // Arrange
        val title = "title"
        val description = "description"
        val addReminder = true
        val todoId = 23L
        viewModel.startAddTodo()
        val todoAdditionState = viewModel.todoAdditionState.value!!
        todoAdditionState.titleInputState.onTitleValueChanged(title)
        todoAdditionState.descriptionInputState.onDescriptionValueChanged(description)
        todoAdditionState.onAddReminderChanged(addReminder)
        whenever(addOrUpdateTodoUseCase(org.mockito.kotlin.any())).thenReturn(todoId)
        val onNavigateToAddReminderMock = mock<(Long) -> Unit>()

        // Act
        viewModel.completeAddTodo(onNavigateToAddReminderMock)
        advanceUntilIdle()

        // Assert
        verify(onNavigateToAddReminderMock, times(1))(todoId)
    }

    @Test
    fun `removing a TODO is done after a delay`() = runTest {
        // Arrange
        val todoId = 1L

        // Act
        viewModel.removeTodo(todoId)
        advanceUntilIdle()

        // Assert
        verify(removeTodoUseCase, times(1))(todoId)
    }

    @Test
    fun `when the process to remove a TODO is undone then the TODO is not removed`() = runTest {
        // Arrange
        val todoId = 1L

        // Act
        viewModel.removeTodo(todoId)
        advanceTimeBy(1000L)
        viewModel.undoTodoRemoval()
        advanceUntilIdle()

        // Assert
        verify(removeTodoUseCase, never())(todoId)
    }

    @Test
    fun `when undoing a todo removal only the todo removals that have not yet completed are undone`() = runTest {
        // Arrange
        val todo1Id = 1L
        val todo2Id = 2L

        // Act
        viewModel.removeTodo(todo1Id)
        advanceTimeBy(4000L)
        viewModel.removeTodo(todo2Id)
        advanceTimeBy(1000L)
        viewModel.undoTodoRemoval()
        advanceUntilIdle()

        // Assert
        verify(removeTodoUseCase, times(1))(todo1Id)
        verify(removeTodoUseCase, never())(todo2Id)
    }

    @Test
    fun `observing reminders for a TODO id returns oa flow of list of reminders`() {
        // Arrange
        val todoId = 1L
        val reminderListFlow: Flow<List<Reminder>> = flowOf(listOf())
        whenever(observeRemindersForTodoUseCase(todoId)).thenReturn(reminderListFlow)

        // Act
        val result = viewModel.observeRemindersForTodo(todoId)

        // Assert
        Assert.assertEquals(reminderListFlow, result)
    }

    @Test
    fun `editing a reminder updates the state with the reminder to edit`() {
        // Arrange
        val reminder = mock<Reminder>()

        // Act
        viewModel.editReminder(reminder)

        // Assert
        Assert.assertEquals(reminder, viewModel.editReminderState.value)
    }

    @Test
    fun `adding a reminder for a TODO is updates the state with the TODO id to get a new reminder`() {
        // Arrange
        val todoId = 1L

        // Act
        viewModel.addReminderForTodo(todoId)

        // Assert
        Assert.assertEquals(todoId, viewModel.addReminderState.value)
    }

    private fun mockString(length: Int): String {
        if (length <= 0) {
            return ""
        }

        var mockString = ""
        for (i in 0..length) {
            mockString += (i % 10).toString()
        }

        return mockString
    }
}