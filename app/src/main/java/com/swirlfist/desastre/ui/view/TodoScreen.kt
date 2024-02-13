package com.swirlfist.desastre.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.swirlfist.desastre.R
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.ui.theme.DesastreTheme
import com.swirlfist.desastre.ui.viewmodel.TodoEditState
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

    val todoEditState = todoScreenViewModel.todoEditState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
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

    Scaffold(
        modifier = Modifier.padding(16.dp),
        content = { paddingValues ->
            Todo(
                todo = todo,
                reminders = reminders,
                todoEditState = todoEditState,
                paddingValues = paddingValues,
                onAddReminder = todoScreenViewModel::addReminderForTodo,
                onEditReminder = todoScreenViewModel::editReminder,
            )
        },
        floatingActionButton = @Composable {
            TodoEditFloatingButtons(
                isEditingTodo = todoEditState.isEditing,
                onEditClick = { todoScreenViewModel.editTodo(todo) },
                onDoneClick = { todoScreenViewModel.finishEditTodo(todo) },
                onCancelClick = todoScreenViewModel::cancelEditTodo,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    )
}

@Composable
fun TodoEditFloatingButtons(
    isEditingTodo: Boolean,
    onEditClick: () -> Unit = {},
    onDoneClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isEditingTodo) {
            FloatingActionButton(
                shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 50)),
                containerColor = MaterialTheme.colorScheme.secondary,
                onClick = onCancelClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.cancel),
                )
            }
            FloatingActionButton(
                shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 50)),
                containerColor = MaterialTheme.colorScheme.secondary,
                onClick = onDoneClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(R.string.save),
                )
            }
        } else {
            FloatingActionButton(
                shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 50)),
                containerColor = MaterialTheme.colorScheme.secondary,
                onClick = onEditClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit),
                )
            }
        }
    }
}

@Composable
fun Todo(
    todo: Todo,
    reminders: List<Reminder>,
    todoEditState: TodoEditState = TodoEditState(),
    paddingValues: PaddingValues = PaddingValues(16.dp),
    onAddReminder: (Long) -> Unit = {},
    onEditReminder: (Reminder) -> Unit = {},
) {
    val backgroundColor = MaterialTheme.colorScheme.surface

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(backgroundColor),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (todoEditState.isEditing) {
            TodoTitleInput(
                todoTitleInputState = todoEditState.titleInputState,
            )
        } else {
            Text(
                text = todo.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
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
            if (todoEditState.isEditing) {
                TodoDescriptionInput(
                    modifier = Modifier.fillMaxHeight(),
                    todoDescriptionInputState = todoEditState.descriptionInputState,
                    nofDescriptionLines = Int.MAX_VALUE,
                )
            } else {
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
}

@Preview
@Composable
fun TodoEditFloatingButtonsEditingPreview() {
    DesastreTheme {
        TodoEditFloatingButtons(
            isEditingTodo = true,
        )
    }
}

@Preview
@Composable
fun TodoEditFloatingButtonsNotEditingPreview() {
    DesastreTheme {
        TodoEditFloatingButtons(
            isEditingTodo = false,
        )
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
        )
    }
}