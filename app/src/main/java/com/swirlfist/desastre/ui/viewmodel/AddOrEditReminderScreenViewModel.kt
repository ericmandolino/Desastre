package com.swirlfist.desastre.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.data.useCase.IAddReminderUseCase
import com.swirlfist.desastre.data.useCase.IObserveReminderUseCase
import com.swirlfist.desastre.data.useCase.IObserveTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AddOrEditReminderScreenViewModel @Inject constructor(
    private val observeTodoUseCase: IObserveTodoUseCase,
    private val observeReminderUseCase: IObserveReminderUseCase,
    private val addReminderUseCase: IAddReminderUseCase,
) : ViewModel()  {
    private val _reminderAddOrUpdateState = MutableStateFlow(createReminderAddOrUpdateState())
    val reminderAddOrUpdateState = _reminderAddOrUpdateState.asStateFlow()

    fun observeTodo(todoId: Long): Flow<Todo?> {
        return observeTodoUseCase(todoId)
    }

    fun observeReminder(reminderId: Long): Flow<Reminder?> {
        return observeReminderUseCase(reminderId)
    }

    fun initializeState(
        todoId: Long,
        reminder: Reminder?,
    ) {
        if (reminder == null) {
            _reminderAddOrUpdateState.update { state ->
                state.copy (
                    todoId = todoId
                )
            }
        } else {
            _reminderAddOrUpdateState.update { state ->
                val localDate = LocalDate.of(reminder.year, reminder.month, reminder.day)
                state.copy(
                    todoId = reminder.todoId,
                    reminderId = reminder.id,
                    selectedDay = localDate,
                    selectedTime = LocalDateTime.of(
                        localDate,
                        LocalTime.of(reminder.hour, reminder.minute)
                    )
                )
            }
        }
    }

    private fun createReminderAddOrUpdateState(): ReminderAddOrUpdateState {
        return ReminderAddOrUpdateState(
            todoId = 0,
            reminderId = 0,
            daySelected = false,
            timeSelected = false,
            selectedDay = null,
            selectedTime = null,
            onDaySelected = { selectedDay ->
                _reminderAddOrUpdateState.update { state ->
                    state.copy(
                        daySelected = true,
                        selectedDay = selectedDay,
                    )
                }
            },
            onTimeSelected = { selectedTime ->
                val selectedDay = _reminderAddOrUpdateState.value.selectedDay ?: return@ReminderAddOrUpdateState
                val forToday = selectedDay.isEqual(LocalDate.now())
                val newSelectedDay = if (forToday) selectedTime.toLocalDate() else selectedDay

                _reminderAddOrUpdateState.update { state ->
                    state.copy(
                        selectedDay = newSelectedDay,
                        timeSelected = true,
                        selectedTime = selectedTime,
                    )
                }

                viewModelScope.launch {
                    addReminderUseCase(
                        Reminder(
                            id = _reminderAddOrUpdateState.value.reminderId,
                            todoId = _reminderAddOrUpdateState.value.todoId,
                            minute = selectedTime.minute,
                            hour = selectedTime.hour,
                            day = newSelectedDay.dayOfMonth,
                            month = newSelectedDay.monthValue,
                            year = newSelectedDay.year
                        )
                    )
                }
            }
        )
    }
}