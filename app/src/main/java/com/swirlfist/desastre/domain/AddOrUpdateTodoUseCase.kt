package com.swirlfist.desastre.domain

import com.swirlfist.desastre.data.model.Todo

fun interface AddOrUpdateTodoUseCase {
    suspend operator fun invoke(todo: Todo): Long
}