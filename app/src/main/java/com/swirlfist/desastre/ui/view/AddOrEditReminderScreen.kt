package com.swirlfist.desastre.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.swirlfist.desastre.ui.theme.DesastreTheme
import com.swirlfist.desastre.ui.viewmodel.AddOrEditReminderScreenViewModel
import java.time.LocalDate

@Composable
fun AddOrEditReminderScreen(
    addOrEditReminderScreenViewModel: AddOrEditReminderScreenViewModel = hiltViewModel(),
    onReminderCompleted: () -> Unit,
    todoId: Long?,
    reminderId: Long? = null,
) {
    val reminderAddOrUpdateState = addOrEditReminderScreenViewModel.reminderAddOrUpdateState.collectAsState().value

    val todo = if (todoId != null) addOrEditReminderScreenViewModel.observeTodo(todoId).collectAsState(null).value else null
    if (todo == null) {
        TodoNotFound()
        return
    }

    val reminder = if (reminderId != null) addOrEditReminderScreenViewModel.observeReminder(reminderId).collectAsState(null).value else null
    if (reminderId != null && reminder == null) {
        ReminderNotFound()
        return
    }

    LaunchedEffect(reminder) {
        addOrEditReminderScreenViewModel.initializeState(todoId ?: 0, reminder)
    }

    if (!reminderAddOrUpdateState.daySelected) {
        SelectorWrapper {
            DaySelector(
                { selectedDate ->
                    run {
                        reminderAddOrUpdateState.onDaySelected(selectedDate)
                    }
                },
                initialDate = reminderAddOrUpdateState.selectedDay,
            )
        }
    } else if (!reminderAddOrUpdateState.timeSelected) {
        val forToday = reminderAddOrUpdateState.selectedDay?.isEqual(LocalDate.now()) ?: false
        SelectorWrapper {
            TimeSelector(
                forToday,
                { selectedTime ->
                    run {
                        reminderAddOrUpdateState.onTimeSelected(selectedTime)
                        if (forToday && selectedTime.toLocalDate().isAfter(LocalDate.now())) {
                            reminderAddOrUpdateState.onDaySelected(selectedTime.toLocalDate())
                        }
                    }
                },
                initialTime = reminderAddOrUpdateState.selectedTime?.toLocalTime(),
            )
        }
    } else {
        LaunchedEffect(reminderAddOrUpdateState) {
            onReminderCompleted()
        }
    }
}

@Composable
fun SelectorWrapper(
    wrappedSelector: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        wrappedSelector()
    }
}

@Composable
fun ReminderNotFound() {
    NotFound()
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun ReminderNotFoundPreview() {
    DesastreTheme {
        ReminderNotFound()
    }
}