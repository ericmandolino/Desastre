package com.swirlfist.desastre.domain

import com.swirlfist.desastre.data.model.Reminder

fun interface IAddOrUpdateReminderUseCase {
    suspend operator fun invoke(reminder: Reminder): Long
}