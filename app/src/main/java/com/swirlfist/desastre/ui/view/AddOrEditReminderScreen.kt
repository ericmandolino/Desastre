package com.swirlfist.desastre.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swirlfist.desastre.R
import com.swirlfist.desastre.ui.theme.DesastreTheme
import com.swirlfist.desastre.ui.viewmodel.AddOrEditReminderScreenViewModel
import com.swirlfist.desastre.util.toShortFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun AddOrEditReminderScreen(
    addOrEditReminderScreenViewModel: AddOrEditReminderScreenViewModel = hiltViewModel(),
    onReminderCompleted: () -> Unit,
    todoId: Long?,
    reminderId: Long? = null,
) {
    val reminderAddOrUpdateState by addOrEditReminderScreenViewModel.reminderAddOrUpdateState.collectAsStateWithLifecycle()

    val todo = if (todoId != null) addOrEditReminderScreenViewModel.observeTodo(todoId).collectAsStateWithLifecycle(null).value else null
    if (todo == null) {
        TodoNotFound()
        return
    }

    val reminder = if (reminderId != null) addOrEditReminderScreenViewModel.observeReminder(reminderId).collectAsStateWithLifecycle(null).value else null
    if (reminderId != null && reminder == null) {
        ReminderNotFound()
        return
    }

    LaunchedEffect(reminder) {
        addOrEditReminderScreenViewModel.initializeState(todoId ?: 0, reminder)
    }

    if (!reminderAddOrUpdateState.daySelected) {
        SelectDayComponent(
            todoTitle = todo.title,
            initialDate = reminderAddOrUpdateState.selectedDay,
            onDaySelected = { selectedDay -> reminderAddOrUpdateState.onDaySelected(selectedDay) }
        )
    } else if (!reminderAddOrUpdateState.timeSelected) {
        SelectTimeComponent(
            todoTitle = todo.title,
            selectedDay = reminderAddOrUpdateState.selectedDay,
            initialTime = reminderAddOrUpdateState.selectedTime?.toLocalTime(),
            onDaySelected = { selectedDay -> reminderAddOrUpdateState.onDaySelected(selectedDay) },
            onTimeSelected = { selectedTime -> reminderAddOrUpdateState.onTimeSelected(selectedTime) }
        )
    } else {
        LaunchedEffect(reminderAddOrUpdateState) {
            onReminderCompleted()
        }
    }
}

@Composable
private fun SelectDayComponent(
    todoTitle: String,
    initialDate: LocalDate? = null,
    onDaySelected: (LocalDate) -> Unit = {},
) {
    SelectorWrapper(
        headingText = headingTextSettingDay(todoTitle)
    ) {
        DaySelector(
            { selectedDate ->
                run {
                    onDaySelected(selectedDate)
                }
            },
            initialDate = initialDate,
        )
    }
}

@Composable
private fun SelectTimeComponent(
    todoTitle: String,
    selectedDay: LocalDate?,
    initialTime: LocalTime? = null,
    onDaySelected: (LocalDate) -> Unit = {},
    onTimeSelected: (LocalDateTime) -> Unit = {},
) {
    val forToday = selectedDay?.isEqual(LocalDate.now()) ?: false

    SelectorWrapper(
        headingText = if (selectedDay == null) {
            headingTextSettingDay(todoTitle)
        } else {
            headingTextSettingTime(todoTitle, selectedDay)
        }
    ) {
        TimeSelector(
            forToday,
            { selectedTime ->
                run {
                    onTimeSelected(selectedTime)
                    if (forToday && selectedTime.toLocalDate().isAfter(LocalDate.now())) {
                        onDaySelected(selectedTime.toLocalDate())
                    }
                }
            },
            initialTime = initialTime,
        )
    }
}

@Composable
private fun headingTextSettingDay(todoTitle: String) = stringResource(R.string.reminder_info_for_setting_day, todoTitle)

@Composable
private fun headingTextSettingTime(
    todoTitle: String,
    selectedDay: LocalDate
): String {
    val today = LocalDate.now()
    return if (selectedDay.isEqual(today)) {
        stringResource(
            R.string.reminder_info_for_setting_time,
            stringResource(R.string.today).toLowerCase(Locale.current),
            todoTitle
        )
    } else if (selectedDay.isEqual(today.plusDays(1))) {
        stringResource(
            R.string.reminder_info_for_setting_time,
            stringResource(R.string.tomorrow).toLowerCase(Locale.current),
            todoTitle
        )
    } else {
        stringResource(
            R.string.reminder_info_for_setting_time,
            stringResource(R.string.on_this_date, selectedDay.toShortFormat()),
            todoTitle
        )
    }
}

@Composable
fun SelectorWrapper(
    headingText: String,
    wrappedSelector: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = headingText,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        wrappedSelector()
    }
}

@Composable
fun ReminderNotFound() {
    NotFound()
}

@Preview(showBackground = true, heightDp = 420)
@Composable
private fun SelectDayComponentPreview() {
    DesastreTheme {
        SelectDayComponent(
            todoTitle = "Some TODO title"
        )
    }
}

@Preview(showBackground = true, heightDp = 420)
@Composable
private fun SelectTimeComponentPreview() {
    DesastreTheme {
        SelectTimeComponent(
            todoTitle = "Some TODO title",
            selectedDay = LocalDate.now(),
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun ReminderNotFoundPreview() {
    DesastreTheme {
        ReminderNotFound()
    }
}