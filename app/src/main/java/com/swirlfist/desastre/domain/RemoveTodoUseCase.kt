package com.swirlfist.desastre.domain

fun interface RemoveTodoUseCase {
    suspend operator fun invoke(todoId: Long)
}