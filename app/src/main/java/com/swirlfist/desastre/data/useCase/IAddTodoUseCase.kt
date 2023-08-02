package com.swirlfist.desastre.data.useCase

import com.swirlfist.desastre.data.model.Todo

fun interface IAddTodoUseCase {
    suspend fun invoke(todo: Todo)
}