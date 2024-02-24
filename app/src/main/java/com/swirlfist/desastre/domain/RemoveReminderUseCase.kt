package com.swirlfist.desastre.domain

fun interface RemoveReminderUseCase {
    suspend operator fun invoke(reminderId: Long)
}