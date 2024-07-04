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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swirlfist.desastre.R
import com.swirlfist.desastre.data.model.Todo
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
    val reminderAddOrUpdateState = addOrEditReminderScreenViewModel.reminderAddOrUpdateState.collectAsStateWithLifecycle().value

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
        SelectorWrapper(
            todo = todo
        ) {
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
        SelectorWrapper(
            todo = todo
        ) {
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
    todo: Todo,
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
            text = stringResource(R.string.reminder_for, todo.title),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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