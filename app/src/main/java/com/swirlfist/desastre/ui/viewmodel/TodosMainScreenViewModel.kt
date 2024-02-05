package com.swirlfist.desastre.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.desastre.data.ICoroutineDispatcherProvider
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.data.useCase.IAddTodoUseCase
import com.swirlfist.desastre.data.useCase.IObserveRemindersForTodoUseCase
import com.swirlfist.desastre.data.useCase.IObserveTodoListUseCase
import com.swirlfist.desastre.data.useCase.IRemoveTodoUseCase
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

private const val TITLE_MAX_CHARACTERS = 50
private const val DESCRIPTION_MAX_CHARACTERS = 2000
private const val UNDO_TODO_REMOVAL_MILLISECONDS = 3000L

@HiltViewModel
class TodosMainScreenViewModel @Inject constructor(
    private val coroutineDispatcherProvider: ICoroutineDispatcherProvider,
    private val observeTodoListUseCase: IObserveTodoListUseCase,
    private val observeRemindersForTodoUseCase: IObserveRemindersForTodoUseCase,
    private val addTodoUseCase: IAddTodoUseCase,
    private val removeTodoUseCase: IRemoveTodoUseCase,
) : ViewModel()  {
    private val delayedTodoRemovalJobs = mutableMapOf<Long, Job>()

    private val _undoTodoRemovalState = MutableStateFlow(UndoTodoRemovalState())
    val undoTodoRemovalState = _undoTodoRemovalState as StateFlow<UndoTodoRemovalState>

    private val _todoAdditionState = MutableStateFlow<TodoAdditionState?>(null)
    val todoAdditionState = _todoAdditionState as StateFlow<TodoAdditionState?>

    private val _editReminderState = MutableStateFlow<Reminder?>(null)
    val editReminderState = _editReminderState as StateFlow<Reminder?>

    fun observeTodoList(): Flow<List<Todo>> {
        return observeTodoListUseCase()
    }

    fun observeRemindersForTodo(todoId: Long): Flow<List<Reminder>> {
        return observeRemindersForTodoUseCase(todoId)
    }

    fun onStartAddTodoClicked() {
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

        if (newTodoAddition.title.isEmpty()) {
            _todoAdditionState.update { state ->
                state?.copy(
                    showTitleEmptyValidationError = true,
                )
            }
            return
        }

        val todo = Todo(
            id = 0,
            title = newTodoAddition.title,
            description = newTodoAddition.description,
            isDone = false,
        )

        viewModelScope.launch {
            val todoId = addTodoUseCase(todo)
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

        return
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

    fun editReminder(reminder: Reminder?) {
        _editReminderState.update { reminder }
    }

    private fun createTodoAdditionState(): TodoAdditionState {
        return TodoAdditionState(
            title = "",
            showTitleEmptyValidationError = false,
            description = "",
            addReminder = false,
            onTitleChanged = { title ->
                _todoAdditionState.update { state ->
                    state?.copy(
                        title = truncateText(title, TITLE_MAX_CHARACTERS),
                        showTitleEmptyValidationError = false,
                    )
                }
            },
            onDescriptionChanged = { description ->
                _todoAdditionState.update { state ->
                    state?.copy(description = truncateText(description, DESCRIPTION_MAX_CHARACTERS))
                }
            },
            onAddReminderChanged = { addReminder ->
                _todoAdditionState.update { state ->
                    state?.copy(addReminder = addReminder)
                }
            },
        )
    }

    private fun truncateText(input: String, maxLength: Int): String {
        return if (input.length > maxLength) input.substring(0, maxLength) else input
    }
}