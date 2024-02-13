package com.swirlfist.desastre.domain

fun interface IRemoveTodoUseCase {
    suspend operator fun invoke(todoId: Long)
}