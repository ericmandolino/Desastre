package com.swirlfist.desastre.data.useCase

import com.swirlfist.desastre.data.model.Reminder
import kotlinx.coroutines.flow.Flow

fun interface IObserveReminderUseCase {
    operator fun invoke(reminderId: Long): Flow<Reminder?>
}