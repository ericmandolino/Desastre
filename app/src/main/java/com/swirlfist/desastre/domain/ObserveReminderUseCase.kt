package com.swirlfist.desastre.domain

import com.swirlfist.desastre.data.model.Reminder
import kotlinx.coroutines.flow.Flow

fun interface ObserveReminderUseCase {
    operator fun invoke(reminderId: Long): Flow<Reminder?>
}