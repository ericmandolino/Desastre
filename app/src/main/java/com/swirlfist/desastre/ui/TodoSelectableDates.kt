package com.swirlfist.desastre.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
class TodoSelectableDates(
    private val minDate: LocalDate
) : SelectableDates {

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val utcDate = LocalDate.from(Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.systemDefault()))
        return utcDate.isEqual(minDate) || utcDate.isAfter(minDate)
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year >= minDate.year
    }
}