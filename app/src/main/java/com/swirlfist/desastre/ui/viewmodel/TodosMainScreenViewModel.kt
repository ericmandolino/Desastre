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

private const val UNDO_TODO_REMOVAL_MILLISECONDS = 3000L

@HiltViewModel
class TodosMainScreenViewModel @Inject constructor(
    private val todoRepository: ITodoRepository
) : ViewModel()  {
    private var undoableTodoRemovals by mutableStateOf(mapOf<Long, Int>())
    private var delayedTodoRemovalJobs = mutableMapOf<Long, Job>()

    fun getTodoList(): Flow<List<Todo>> {
        return todoRepository.observeTodos()
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
        todoRepository.removeTodo(id)
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
}