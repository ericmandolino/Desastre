package com.swirlfist.desastre.util

import com.swirlfist.desastre.data.model.Reminder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun Reminder.isForToday() = LocalDate.now().isEqual(this.time.toLocalDate())

fun Reminder.isForTomorrow() = LocalDate.now().plusDays(1).isEqual(this.time.toLocalDate())

fun Reminder.isInThePast() = this.time.isBefore(LocalDateTime.now())

fun LocalDate.toShortFormat(): String = this.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))

fun LocalTime.toShortFormat(): String = this.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))