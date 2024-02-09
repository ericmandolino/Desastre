package com.swirlfist.desastre.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.data.useCase.IObserveRemindersForTodoUseCase
import com.swirlfist.desastre.data.useCase.IObserveTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TodoScreenViewModel @Inject constructor(
    private val observeTodoUseCase: IObserveTodoUseCase,
    private val observeRemindersForTodoUseCase: IObserveRemindersForTodoUseCase,
): ViewModel() {
    private val _addReminderState = MutableStateFlow<Long?>(null)
    val addReminderState = _addReminderState as StateFlow<Long?>

    private val _editReminderState = MutableStateFlow<Reminder?>(null)
    val editReminderState = _editReminderState as StateFlow<Reminder?>

    fun observeTodo(todoId: Long): Flow<Todo?> {
        return observeTodoUseCase(todoId)
    }

    fun observeRemindersForTodo(todoId: Long): Flow<List<Reminder>> {
        return observeRemindersForTodoUseCase(todoId)
    }

    fun addReminderForTodo(todoId: Long?) {
        _addReminderState.update { todoId }
    }

    fun editReminder(reminder: Reminder?) {
        _editReminderState.update { reminder }
    }
}