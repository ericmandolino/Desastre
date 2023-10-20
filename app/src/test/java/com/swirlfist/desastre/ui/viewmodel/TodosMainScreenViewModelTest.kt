package com.swirlfist.desastre.ui.viewmodel

import com.swirlfist.desastre.data.ICoroutineDispatcherProvider
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.data.useCase.IAddTodoUseCase
import com.swirlfist.desastre.data.useCase.IObserveTodoListUseCase
import com.swirlfist.desastre.data.useCase.IRemoveTodoUseCase
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
    lateinit var coroutineDispatcherProvider: ICoroutineDispatcherProvider

    @Mock
    lateinit var observeTodoListUseCase: IObserveTodoListUseCase

    @Mock
    lateinit var addTodoUseCase: IAddTodoUseCase

    @Mock
    lateinit var removeTodoUseCase: IRemoveTodoUseCase

    private lateinit var viewModel: TodosMainScreenViewModel

    @Before
    fun setUp() {
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        whenever(coroutineDispatcherProvider.getMain()).thenReturn(testDispatcher)
        viewModel = TodosMainScreenViewModel(
            coroutineDispatcherProvider,
            observeTodoListUseCase,
            addTodoUseCase,
            removeTodoUseCase,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun observeTodoList_returnsFlowOfTodoList() {
        // Arrange
        val todoListFlow: Flow<List<Todo>> = flowOf(listOf())
        whenever(observeTodoListUseCase()).thenReturn(todoListFlow)

        // Act
        val result = viewModel.observeTodoList()

        // Assert
        Assert.assertEquals(todoListFlow, result)
    }

    @Test
    fun onStartAddTodoClicked_createsTodoAdditionState() {
        // Arrange

        // Act
        viewModel.onStartAddTodoClicked()
        val todoAdditionState = viewModel.getTodoAdditionState().value!!

        // Assert
        Assert.assertEquals("", todoAdditionState.title)
        Assert.assertEquals("", todoAdditionState.description)
        Assert.assertFalse(todoAdditionState.showTitleEmptyValidationError)
        Assert.assertFalse(todoAdditionState.addReminder)
    }

    @Test
    fun onCancelAddTodoClicked_todoAdditionStateCleared() {
        // Arrange
        viewModel.onStartAddTodoClicked()
        Assert.assertNotNull(viewModel.getTodoAdditionState().value)

        // Act
        viewModel.onCancelAddTodoClicked()

        // Assert
        Assert.assertNull(viewModel.getTodoAdditionState().value)
    }

    @Test
    fun addingTodo_titleChanged_updatesTodoAdditionStateWithNewTitle() {
        // Arrange
        viewModel.onStartAddTodoClicked()
        var todoAdditionState = viewModel.getTodoAdditionState().value!!
        val newTitle = "new title"

        // Act
        todoAdditionState.onTitleChanged(newTitle)
        todoAdditionState = viewModel.getTodoAdditionState().value!!

        // Assert
        Assert.assertEquals(newTitle, todoAdditionState.title)
    }

    @Test
    fun addingTodo_titleChangedLong_updatesTodoAdditionStateWithTruncatedTitle() {
        // Arrange
        viewModel.onStartAddTodoClicked()
        var todoAdditionState = viewModel.getTodoAdditionState().value!!
        val newTitle = mockString(55)

        // Act
        todoAdditionState.onTitleChanged(newTitle)
        todoAdditionState = viewModel.getTodoAdditionState().value!!

        // Assert
        Assert.assertEquals(newTitle.substring(0, 50), todoAdditionState.title)
    }

    @Test
    fun addingTodo_descriptionChanged_updatesTodoAdditionStateWithNewDescription() {
        // Arrange
        viewModel.onStartAddTodoClicked()
        var todoAdditionState = viewModel.getTodoAdditionState().value!!
        val newDescription = "new description"

        // Act
        todoAdditionState.onDescriptionChanged(newDescription)
        todoAdditionState = viewModel.getTodoAdditionState().value!!

        // Assert
        Assert.assertEquals(newDescription, todoAdditionState.description)
    }

    @Test
    fun addingTodo_descriptionChangedLong_updatesTodoAdditionStateWithTruncatedDescription() {
        // Arrange
        viewModel.onStartAddTodoClicked()
        var todoAdditionState = viewModel.getTodoAdditionState().value!!
        val newDescription = mockString(2010)

        // Act
        todoAdditionState.onDescriptionChanged(newDescription)
        todoAdditionState = viewModel.getTodoAdditionState().value!!

        // Assert
        Assert.assertEquals(newDescription.substring(0, 2000), todoAdditionState.description)
    }

    @Test
    fun addingTodo_addReminderChanged_updatesTodoAdditionStateWithNewValue() {
        // Arrange
        viewModel.onStartAddTodoClicked()
        var todoAdditionState = viewModel.getTodoAdditionState().value!!

        // Act
        todoAdditionState.onAddReminderChanged(false)
        todoAdditionState.onAddReminderChanged(true)
        todoAdditionState = viewModel.getTodoAdditionState().value!!

        // Assert
        Assert.assertTrue(todoAdditionState.addReminder)
    }

    @Test
    fun onCompleteAddTodoClicked_emptyTitle_updatesTodoAdditionStateWithError() {
        // Arrange
        viewModel.onStartAddTodoClicked()
        var todoAdditionState = viewModel.getTodoAdditionState().value!!

        // Act
        todoAdditionState.onTitleChanged("")
        viewModel.onCompleteAddTodoClicked {}
        todoAdditionState = viewModel.getTodoAdditionState().value!!

        // Assert
        Assert.assertTrue(todoAdditionState.showTitleEmptyValidationError)
    }

    @Test
    fun onCompleteAddTodoClicked_todoAdded() = runTest {
        // Arrange
        val title = "title"
        val description = "description"
        val addReminder = true
        viewModel.onStartAddTodoClicked()
        val todoAdditionState = viewModel.getTodoAdditionState().value!!
        todoAdditionState.onTitleChanged(title)
        todoAdditionState.onDescriptionChanged(description)
        todoAdditionState.onAddReminderChanged(addReminder)
        whenever(addTodoUseCase(org.mockito.kotlin.any())).thenReturn(1L)

        // Act
        viewModel.onCompleteAddTodoClicked {}
        advanceUntilIdle()

        // Assert
        verify(addTodoUseCase, times(1))(org.mockito.kotlin.check { todo ->
            Assert.assertEquals(0, todo.id)
            Assert.assertEquals(title, todo.title)
            Assert.assertEquals(description, todo.description)
            Assert.assertFalse(todo.isDone)
        })
    }

    @Test
    fun onCompleteAddTodoClicked_todoAdditionStateCleared() = runTest {
        // Arrange
        val title = "title"
        viewModel.onStartAddTodoClicked()
        val todoAdditionState = viewModel.getTodoAdditionState().value!!
        todoAdditionState.onTitleChanged(title)
        whenever(addTodoUseCase(org.mockito.kotlin.any())).thenReturn(1L)

        // Act
        viewModel.onCompleteAddTodoClicked {}
        advanceUntilIdle()

        // Assert
        Assert.assertNull(viewModel.getTodoAdditionState().value)
    }

    @Test
    fun onCompleteAddTodoClicked_addReminderSelected_navigatesToAddReminder() = runTest {
        // Arrange
        val title = "title"
        val description = "description"
        val addReminder = true
        val todoId = 23L
        viewModel.onStartAddTodoClicked()
        val todoAdditionState = viewModel.getTodoAdditionState().value!!
        todoAdditionState.onTitleChanged(title)
        todoAdditionState.onDescriptionChanged(description)
        todoAdditionState.onAddReminderChanged(addReminder)
        whenever(addTodoUseCase(org.mockito.kotlin.any())).thenReturn(todoId)
        val onNavigateToAddReminderMock = mock<(Long) -> Unit>()

        // Act
        viewModel.onCompleteAddTodoClicked(onNavigateToAddReminderMock)
        advanceUntilIdle()

        // Assert
        verify(onNavigateToAddReminderMock, times(1))(todoId)
    }

    @Test
    fun removeTodo_todoRemovedAfterDelay() = runTest {
        // Arrange
        val todoId = 1L

        // Act
        viewModel.removeTodo(todoId)
        advanceUntilIdle()

        // Assert
        verify(removeTodoUseCase, times(1))(todoId)
    }

    @Test
    fun removeTodo_removalUndone_todoNotRemoved() = runTest {
        // Arrange
        val todoId = 1L

        // Act
        viewModel.removeTodo(todoId)
        val undoableRemovalState = viewModel.getUndoableRemovalState()
        advanceTimeBy(1000L)
        undoableRemovalState.onUndoClicked(todoId)
        advanceUntilIdle()

        // Assert
        verify(removeTodoUseCase, never())(todoId)
    }

    @Test
    fun removeTodo_undoProgressUpdated() = runTest {
        // Arrange
        val todoId = 1L

        // Act
        viewModel.removeTodo(todoId)
        advanceTimeBy(1000L)
        val undoableRemovalState = viewModel.getUndoableRemovalState()
        val progress = undoableRemovalState.undoableTodoRemovals[todoId]

        // Assert
        Assert.assertEquals(33, progress)
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