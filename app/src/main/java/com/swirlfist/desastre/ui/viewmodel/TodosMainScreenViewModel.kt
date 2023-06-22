package com.swirlfist.desastre.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.swirlfist.desastre.data.ITodoRepository
import com.swirlfist.desastre.data.model.Todo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TodosMainScreenViewModel @Inject constructor(
    private val todoRepository: ITodoRepository
) : ViewModel()  {
    fun getTodoList(): Flow<List<Todo>> {
        return todoRepository.observeTodos()
    }

    fun removeTodo(id: Long) {
        todoRepository.removeTodo(id)
    }
}