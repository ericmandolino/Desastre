package com.swirlfist.desastre.domain

import com.swirlfist.desastre.data.model.Reminder
import kotlinx.coroutines.flow.Flow

fun interface IObserveRemindersForTodoUseCase {
    operator fun invoke(todoId: Long): Flow<List<Reminder>>
}