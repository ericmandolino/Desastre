package com.swirlfist.desastre.domain

import com.swirlfist.desastre.data.model.Todo

fun interface IAddOrUpdateTodoUseCase {
    suspend operator fun invoke(todo: Todo): Long
}