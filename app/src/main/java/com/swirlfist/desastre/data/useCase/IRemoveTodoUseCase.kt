package com.swirlfist.desastre.data.useCase

fun interface IRemoveTodoUseCase {
    suspend fun invoke(todoId: Long)
}