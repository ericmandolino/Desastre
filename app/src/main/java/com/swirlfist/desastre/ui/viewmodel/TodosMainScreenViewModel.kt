package com.swirlfist.desastre.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.desastre.data.ITodoRepository
import com.swirlfist.desastre.data.model.Todo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val UNDO_TODO_REMOVAL_MILLISECONDS = 5000L

@HiltViewModel
class TodosMainScreenViewModel @Inject constructor(
    private val todoRepository: ITodoRepository
) : ViewModel()  {
    private var undoableTodoRemovalIds by mutableStateOf(listOf<Long>())
    private var delayedTodoRemovalJobs = mutableMapOf<Long, Job>()

    fun getTodoList(): Flow<List<Todo>> {
        return todoRepository.observeTodos()
    }

    fun removeTodo(id: Long) {
        undoableTodoRemovalIds = undoableTodoRemovalIds + id
        val removalJob = viewModelScope.launch { delayedRemoveTodo(id) }
        delayedTodoRemovalJobs[id] = removalJob
    }

    private suspend fun delayedRemoveTodo(id: Long) {
        delay(UNDO_TODO_REMOVAL_MILLISECONDS)
        todoRepository.removeTodo(id)
        delayedTodoRemovalJobs.remove(id)
    }

    fun getUndoableRemovalState(): UndoableTodoRemovalState {
        return UndoableTodoRemovalState(
            undoableTodoRemovalIds,
            onUndoClicked = { todoId ->
                delayedTodoRemovalJobs.remove(todoId)?.cancel()
                undoableTodoRemovalIds = undoableTodoRemovalIds - todoId },
        )
    }
}