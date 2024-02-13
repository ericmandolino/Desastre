package com.swirlfist.desastre.domain

import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.flow.Flow

fun interface IObserveTodoUseCase {
    operator fun invoke(todoId: Long): Flow<Todo?>
}