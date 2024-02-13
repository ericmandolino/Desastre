package com.swirlfist.desastre.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.data.useCase.IAddOrUpdateTodoUseCase
import com.swirlfist.desastre.data.useCase.IObserveRemindersForTodoUseCase
import com.swirlfist.desastre.data.useCase.IObserveTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoScreenViewModel @Inject constructor(
    private val observeTodoUseCase: IObserveTodoUseCase,
    private val observeRemindersForTodoUseCase: IObserveRemindersForTodoUseCase,
    private val addOrUpdateTodoUseCase: IAddOrUpdateTodoUseCase,
): ViewModel() {

    private val _todoEditState = MutableStateFlow(createTodoEditState())
    val todoEditState = _todoEditState as StateFlow<TodoEditState>

    private val _addReminderState = MutableStateFlow<Long?>(null)
    val addReminderState = _addReminderState as StateFlow<Long?>

    private val _editReminderState = MutableStateFlow<Reminder?>(null)
    val editReminderState = _editReminderState as StateFlow<Reminder?>

    fun observeTodo(todoId: Long): Flow<Todo?> {
        return observeTodoUseCase(todoId)
    }

    fun editTodo(todo: Todo) {
        _todoEditState.update { editTodoState ->
            editTodoState.copy(
                isEditing = true,
                titleInputState = editTodoState.titleInputState.updateTitleText(todo.title),
                descriptionInputState = editTodoState.descriptionInputState.updateDescriptionText(todo.description),
            )
        }
    }

    fun cancelEditTodo() {
        endEditing()
    }

    fun finishEditTodo(todo: Todo) {
        if (!_todoEditState.value.isEditing) {
            return
        }

        val validatedInputTitleState = _todoEditState.value.titleInputState.validateTitleText()

        if (validatedInputTitleState.titleValidation != TodoTitleValidationResult.SUCCESS) {
            _todoEditState.update { todoEditState ->
                todoEditState.copy(
                    titleInputState = validatedInputTitleState
                )
            }
            return
        }

        viewModelScope.launch {
            addOrUpdateTodoUseCase(
                todo.copy(
                    title = _todoEditState.value.titleInputState.titleText,
                    description = _todoEditState.value.descriptionInputState.descriptionText,
                )
            )
        }

        endEditing()
    }

    private fun endEditing() {
        _todoEditState.update { editTodoState ->
            editTodoState.copy(
                isEditing = false,
                titleInputState = editTodoState.titleInputState.updateTitleText(""),
                descriptionInputState = editTodoState.descriptionInputState.updateDescriptionText(""),
            )
        }
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

    private fun createTodoEditState() = TodoEditState(
        isEditing = false,
        titleInputState = TodoTitleInputState(
            onTitleValueChanged = ::onTodoEditTitleValueChanged
        ),
        descriptionInputState = TodoDescriptionInputState(
            onDescriptionValueChanged = ::onTodoEditDescriptionValueChanged
        )
    )

    private fun onTodoEditTitleValueChanged(title: String) {
        _todoEditState.update { todoEditState ->
            todoEditState.copy(
                titleInputState = todoEditState.titleInputState.updateTitleText(title),
            )
        }
    }

    private fun onTodoEditDescriptionValueChanged(description: String) {
        _todoEditState.update { todoEditState ->
            todoEditState.copy(
                descriptionInputState = todoEditState.descriptionInputState.updateDescriptionText(description)
            )
        }
    }
}