package com.swirlfist.desastre.data.useCase

import com.swirlfist.desastre.data.model.Reminder

fun interface IAddOrUpdateReminderUseCase {
    suspend operator fun invoke(reminder: Reminder): Long
}