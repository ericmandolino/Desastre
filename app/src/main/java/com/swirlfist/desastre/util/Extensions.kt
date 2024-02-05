package com.swirlfist.desastre.util

import com.swirlfist.desastre.data.model.Reminder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun Reminder.isForToday(): Boolean {
    val today = LocalDate.now()
    return day == today.dayOfMonth && month == today.monthValue && year == today.year
}

fun Reminder.isForTomorrow(): Boolean {
    val tomorrow = LocalDate.now().plusDays(1)
    return day == tomorrow.dayOfMonth && month == tomorrow.monthValue && year == tomorrow.year
}

fun Reminder.isInThePast() = this.toLocalDateTime().isBefore(LocalDateTime.now())

fun Reminder.toLocalDateTime(): LocalDateTime = LocalDateTime.of(
    this.year,
    this.month,
    this.day,
    this.hour,
    this.minute
)

fun LocalDate.toShortFormat(): String = this.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))

fun LocalTime.toShortFormat(): String = this.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))