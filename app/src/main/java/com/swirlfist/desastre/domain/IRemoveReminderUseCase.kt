package com.swirlfist.desastre.domain

fun interface IRemoveReminderUseCase {
    suspend operator fun invoke(reminderId: Long)
}