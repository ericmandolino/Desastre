package com.swirlfist.desastre.data.useCase

fun interface IRemoveReminderUseCase {
    suspend operator fun invoke(reminderId: Long)
}