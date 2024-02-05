package com.swirlfist.desastre.ui.viewmodel

import java.time.LocalDate
import java.time.LocalDateTime

data class ReminderAddOrUpdateState(
    val todoId: Long,
    val reminderId: Long,
    val daySelected: Boolean,
    val timeSelected: Boolean,
    val selectedDay: LocalDate?,
    val selectedTime: LocalDateTime?,
    val onDaySelected: (LocalDate) -> Unit,
    val onTimeSelected: (LocalDateTime) -> Unit,
)
