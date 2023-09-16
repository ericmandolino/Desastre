package com.swirlfist.desastre.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.data.useCase.IObserveTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AddOrEditReminderScreenViewModel @Inject constructor(
    private val observeTodoUseCase: IObserveTodoUseCase,
) : ViewModel()  {
    fun observeTodo(todoId: Long): Flow<Todo?> {
        return observeTodoUseCase.invoke(todoId)
    }
}