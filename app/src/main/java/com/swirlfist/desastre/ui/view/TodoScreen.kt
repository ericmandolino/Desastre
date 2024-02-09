package com.swirlfist.desastre.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.ui.theme.DesastreTheme
import com.swirlfist.desastre.ui.viewmodel.TodoScreenViewModel

@Composable
fun TodoScreen(
    todoScreenViewModel: TodoScreenViewModel = hiltViewModel(),
    todoId: Long?,
    onNavigateToAddReminder: (todoId: Long) -> Unit,
    onNavigateToEditReminder: (todoId: Long, reminderId: Long) -> Unit,
) {
    val todo = if (todoId != null) todoScreenViewModel.observeTodo(todoId).collectAsState(null).value else null
    if (todo == null) {
        TodoNotFound()
        return
    }

    val reminders =  todoScreenViewModel.observeRemindersForTodo(todo.id).collectAsState(initial = listOf()).value

    val todoIdToGetNewReminder = todoScreenViewModel.addReminderState.collectAsState().value
    val reminderToEdit = todoScreenViewModel.editReminderState.collectAsState().value

    LaunchedEffect(todoIdToGetNewReminder, reminderToEdit) {
        if (todoIdToGetNewReminder != null) {
            onNavigateToAddReminder(todoIdToGetNewReminder)
            todoScreenViewModel.addReminderForTodo(null)
        } else if (reminderToEdit != null) {
            onNavigateToEditReminder(reminderToEdit.todoId, reminderToEdit.id)
            todoScreenViewModel.editReminder(null)
        }
    }

    Todo(
        todo,
        reminders,
        onAddReminder = todoScreenViewModel::addReminderForTodo,
        onEditReminder = todoScreenViewModel::editReminder,
    )
}

@Composable
fun Todo(
    todo: Todo,
    reminders: List<Reminder>,
    onAddReminder: (Long) -> Unit,
    onEditReminder: (Reminder) -> Unit,
) {
    val backgroundColor = MaterialTheme.colorScheme.surface

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .background(backgroundColor),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = todo.title,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Reminders(
            reminders = reminders,
            backgroundColor = backgroundColor,
            onAddReminder = { onAddReminder(todo.id) },
            onEditReminder = onEditReminder,
        )
        Box(
            modifier = Modifier
                .weight(1F),
        ) {
            val descriptionBackgroundColor = MaterialTheme.colorScheme.secondaryContainer
            val textScrollState = rememberScrollState()
            Text(
                text = todo.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxSize()
                    .background(descriptionBackgroundColor)
                    .padding(8.dp)
                    .verticalScroll(textScrollState),
            )
            if (textScrollState.canScrollForward) {
                VerticalScrollableGradient(
                    color = descriptionBackgroundColor,
                    isTop = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (textScrollState.canScrollBackward) {
                VerticalScrollableGradient(
                    color = descriptionBackgroundColor,
                    isTop = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 480, widthDp = 320)
@Composable
fun TodoPreviewNoDescription() {
    DesastreTheme {
        Todo(
            todo = PreviewUtil.mockTodo(
                title = PreviewUtil.mockTitle(),
                description = "",
                isDone = false,
            ),
            reminders = listOf(),
            onAddReminder = {},
            onEditReminder = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 480, widthDp = 320)
@Composable
fun TodoPreviewShortDescription() {
    DesastreTheme {
        Todo(
            todo = PreviewUtil.mockTodo(
                title = PreviewUtil.mockTitle(),
                description = "Short description",
                isDone = false,
            ),
            reminders = PreviewUtil.mockReminders(5),
            onAddReminder = {},
            onEditReminder = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 480, widthDp = 320)
@Composable
fun TodoPreviewLongDescription() {
    DesastreTheme {
        Todo(
            todo = PreviewUtil.mockTodo(
                title = PreviewUtil.mockTitle(),
                description = PreviewUtil.mockDescription(),
                isDone = false,
            ),
            reminders = listOf(),
            onAddReminder = {},
            onEditReminder = {},
        )
    }
}