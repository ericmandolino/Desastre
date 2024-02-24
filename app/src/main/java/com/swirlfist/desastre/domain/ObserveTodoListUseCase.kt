package com.swirlfist.desastre.domain

import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.flow.Flow

fun interface ObserveTodoListUseCase {
    operator fun invoke(): Flow<List<Todo>>
}