package com.swirlfist.desastre.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.desastre.data.ICoroutineDispatcherProvider
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.data.useCase.IAddTodoUseCase
import com.swirlfist.desastre.data.useCase.IObserveTodoListUseCase
import com.swirlfist.desastre.data.useCase.IRemoveTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val addTodoUseCase: IAddTodoUseCase,
    private val removeTodoUseCase: IRemoveTodoUseCase,
) : ViewModel()  {
    private var undoableTodoRemovals by mutableStateOf(mapOf<Long, Int>())
    private var delayedTodoRemovalJobs = mutableMapOf<Long, Job>()
    private val todoAdditionState = MutableStateFlow<TodoAdditionState?>(null)

    fun observeTodoList(): Flow<List<Todo>> {
        return observeTodoListUseCase()
    }

    fun getTodoAdditionState(): StateFlow<TodoAdditionState?> {
        return todoAdditionState.asStateFlow()
    }

    fun onStartAddTodoClicked() {
        todoAdditionState.update {
            createTodoAdditionState()
        }
    }

    fun onCancelAddTodoClicked() {
        todoAdditionState.update {
            null
        }
    }

    fun onCompleteAddTodoClicked(
        onNavigateToAddReminder: (todoId: Long) -> Unit,
    ) {
        val newTodoAddition = todoAdditionState.value ?: return

        if (newTodoAddition.title.isEmpty()) {
            todoAdditionState.update { state ->
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

        todoAdditionState.update {
            null
        }

        return
    }

    fun removeTodo(id: Long) {
        undoableTodoRemovals = undoableTodoRemovals + mapOf(Pair(id, 0))
        val removalJob = viewModelScope.launch { delayedRemoveTodo(id) }
        delayedTodoRemovalJobs[id] = removalJob
    }

    private suspend fun delayedRemoveTodo(id: Long) {
        delayWithProgressUpdate(
            todoId = id,
            delayMilliseconds = UNDO_TODO_REMOVAL_MILLISECONDS
        )
        removeTodoUseCase(id)
        delayedTodoRemovalJobs.remove(id)
    }

    private suspend fun delayWithProgressUpdate(
        todoId: Long,
        delayMilliseconds: Long,
    ) {
        val onePercentDelay = delayMilliseconds / 100
        for (i in 1..100) {
            delay(onePercentDelay)
            val currentProgress = undoableTodoRemovals[todoId]?: 0
            undoableTodoRemovals = undoableTodoRemovals - todoId
            undoableTodoRemovals = undoableTodoRemovals + Pair(todoId, currentProgress + 1)
        }
    }

    fun getUndoableRemovalState(): UndoableTodoRemovalState {
        return UndoableTodoRemovalState(
            undoableTodoRemovals,
            onUndoClicked = { todoId ->
                delayedTodoRemovalJobs.remove(todoId)?.cancel()
                undoableTodoRemovals = undoableTodoRemovals - todoId },
        )
    }

    private fun createTodoAdditionState(): TodoAdditionState {
        return TodoAdditionState(
            title = "",
            showTitleEmptyValidationError = false,
            description = "",
            addReminder = false,
            onTitleChanged = { title ->
                todoAdditionState.update { state ->
                    state?.copy(
                        title = truncateText(title, TITLE_MAX_CHARACTERS),
                        showTitleEmptyValidationError = false,
                    )
                }
            },
            onDescriptionChanged = { description ->
                todoAdditionState.update { state ->
                    state?.copy(description = truncateText(description, DESCRIPTION_MAX_CHARACTERS))
                }
            },
            onAddReminderChanged = { addReminder ->
                todoAdditionState.update { state ->
                    state?.copy(addReminder = addReminder)
                }
            },
        )
    }

    private fun truncateText(input: String, maxLength: Int): String {
        return if (input.length > maxLength) input.substring(0, maxLength) else input
    }
}