package com.swirlfist.desastre.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.desastre.data.CoroutineDispatcherProvider
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.domain.AddOrUpdateTodoUseCase
import com.swirlfist.desastre.domain.ObserveRemindersForTodoUseCase
import com.swirlfist.desastre.domain.ObserveTodoListUseCase
import com.swirlfist.desastre.domain.RemoveTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val UNDO_TODO_REMOVAL_MILLISECONDS = 3000L

@HiltViewModel
class TodosMainScreenViewModel @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val observeTodoListUseCase: ObserveTodoListUseCase,
    private val observeRemindersForTodoUseCase: ObserveRemindersForTodoUseCase,
    private val addOrUpdateTodoUseCase: AddOrUpdateTodoUseCase,
    private val removeTodoUseCase: RemoveTodoUseCase,
) : ViewModel()  {
    private val delayedTodoRemovalJobs = mutableMapOf<Long, Job>()

    private val _undoTodoRemovalState = MutableStateFlow(UndoTodoRemovalState())
    val undoTodoRemovalState = _undoTodoRemovalState as StateFlow<UndoTodoRemovalState>

    private val _todoAdditionState = MutableStateFlow<TodoAdditionState?>(null)
    val todoAdditionState = _todoAdditionState as StateFlow<TodoAdditionState?>

    private val _addReminderState = MutableStateFlow<Long?>(null)
    val addReminderState = _addReminderState as StateFlow<Long?>

    private val _editReminderState = MutableStateFlow<Reminder?>(null)
    val editReminderState = _editReminderState as StateFlow<Reminder?>

    fun observeTodoList(): Flow<List<Todo>> {
        return observeTodoListUseCase()
    }

    fun observeRemindersForTodo(todoId: Long): Flow<List<Reminder>> {
        return observeRemindersForTodoUseCase(todoId)
    }

    fun startAddTodo() {
        _todoAdditionState.update {
            createTodoAdditionState()
        }
    }

    fun cancelAddTodo() {
        _todoAdditionState.update {
            null
        }
    }

    fun completeAddTodo(
        onNavigateToAddReminder: (todoId: Long) -> Unit,
    ) {
        val newTodoAddition = todoAdditionState.value ?: return
        val validatedInputTitleState = newTodoAddition.titleInputState.validateTitleText()

        if (validatedInputTitleState.titleValidation != TodoTitleValidationResult.SUCCESS) {
            _todoAdditionState.update { todoAdditionState ->
                todoAdditionState?.copy(
                    titleInputState = validatedInputTitleState
                )
            }
            return
        }

        val todo = Todo(
            id = 0,
            title = newTodoAddition.titleInputState.titleText,
            description = newTodoAddition.descriptionInputState.descriptionText,
            isDone = false,
        )

        viewModelScope.launch {
            val todoId = addOrUpdateTodoUseCase(todo)
            if (!newTodoAddition.addReminder) {
                return@launch
            }
            withContext(coroutineDispatcherProvider.getMain()) {
                onNavigateToAddReminder(todoId)
            }
        }

        _todoAdditionState.update {
            null
        }
    }

    fun removeTodo(id: Long) {
        _undoTodoRemovalState.update { state ->
            state.copy(
                undoableTodoRemovals = state.undoableTodoRemovals + id
            )
        }
        val removalJob = viewModelScope.launch { delayedRemoveTodo(id) }
        delayedTodoRemovalJobs[id] = removalJob
    }

    private suspend fun delayedRemoveTodo(id: Long) {
        delay(UNDO_TODO_REMOVAL_MILLISECONDS)
        delayedTodoRemovalJobs.remove(id)
        _undoTodoRemovalState.update { state ->
            state.copy(
                undoableTodoRemovals = state.undoableTodoRemovals - id
            )
        }
        removeTodoUseCase(id)
    }

    fun undoTodoRemoval() {
        delayedTodoRemovalJobs.values.forEach { job ->
            job.cancel()
        }
        delayedTodoRemovalJobs.clear()
        _undoTodoRemovalState.update { state ->
            state.copy(
                undoableTodoRemovals = listOf()
            )
        }
    }

    fun addReminderForTodo(todoId: Long?) {
        _addReminderState.update { todoId }
    }

    fun editReminder(reminder: Reminder?) {
        _editReminderState.update { reminder }
    }

    private fun createTodoAdditionState() = TodoAdditionState(
        titleInputState = TodoTitleInputState(
            onTitleValueChanged = ::onTodoAdditionTitleValueChanged,
        ),
        descriptionInputState = TodoDescriptionInputState(
            onDescriptionValueChanged = ::onTodoAdditionDescriptionValueChanged,
        ),
        addReminder = false,
        onAddReminderChanged = { addReminder ->
            _todoAdditionState.update { state ->
                state?.copy(addReminder = addReminder)
            }
        },
    )

    private fun onTodoAdditionTitleValueChanged(title: String) {
        _todoAdditionState.update { todoAdditionState ->
            todoAdditionState?.copy(
                titleInputState = todoAdditionState.titleInputState.updateTitleText(title)
            )
        }
    }

    private fun onTodoAdditionDescriptionValueChanged(description: String) {
        _todoAdditionState.update { todoAdditionState ->
            todoAdditionState?.copy(
                descriptionInputState = todoAdditionState.descriptionInputState.updateDescriptionText(description)
            )
        }
    }
}