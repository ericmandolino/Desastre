package com.swirlfist.desastre.data.useCase

fun interface IRemoveTodoUseCase {
    suspend operator fun invoke(todoId: Long)
}