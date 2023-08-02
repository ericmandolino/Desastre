package com.swirlfist.desastre.data.useCase

import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.flow.Flow

fun interface IObserveTodoListUseCase {
    fun invoke(): Flow<List<Todo>>
}