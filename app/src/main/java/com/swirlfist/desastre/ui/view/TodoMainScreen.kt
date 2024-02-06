package com.swirlfist.desastre.ui.view

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.swirlfist.desastre.R
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.ui.theme.DesastreTheme
import com.swirlfist.desastre.ui.viewmodel.TodoAdditionState
import com.swirlfist.desastre.ui.viewmodel.TodosMainScreenViewModel
import com.swirlfist.desastre.ui.viewmodel.UndoTodoRemovalState
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.random.Random

@Composable
fun TodoMainScreen(
    todosMainScreenViewModel: TodosMainScreenViewModel = hiltViewModel(),
    onNavigateToAddReminder: (todoId: Long) -> Unit,
    onNavigateToEditReminder: (todoId: Long, reminderId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val isAddingTodo = todosMainScreenViewModel.todoAdditionState.collectAsState().value != null
    val todoIdToGetNewReminder = todosMainScreenViewModel.addReminderState.collectAsState().value
    val reminderToEdit = todosMainScreenViewModel.editReminderState.collectAsState().value
    val undoTodoRemovalState = todosMainScreenViewModel.undoTodoRemovalState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }
    val todoRemovedString = stringResource(R.string.todo_removed)
    val undoString = stringResource(R.string.undo)

    LaunchedEffect(todoIdToGetNewReminder, reminderToEdit) {
        if (todoIdToGetNewReminder != null) {
            onNavigateToAddReminder(todoIdToGetNewReminder)
            todosMainScreenViewModel.addReminderForTodo(null)
        } else if (reminderToEdit != null) {
            onNavigateToEditReminder(reminderToEdit.todoId, reminderToEdit.id)
            todosMainScreenViewModel.editReminder(null)
        }
    }

    LaunchedEffect(undoTodoRemovalState) {
        val currentSnackbarData = snackbarHostState.currentSnackbarData
        if (undoTodoRemovalState.undoableTodoRemovals.isEmpty()) {
            currentSnackbarData?.dismiss()
        } else if (currentSnackbarData == null) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = todoRemovedString,
                    actionLabel = undoString,
                    withDismissAction = false,
                    duration = SnackbarDuration.Indefinite,

                    )
                when (result) {
                    SnackbarResult.Dismissed -> {
                    }
                    SnackbarResult.ActionPerformed -> {
                        todosMainScreenViewModel.undoTodoRemoval()
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.padding(8.dp),
        content = { paddingValues ->  TodoMainScreenContent(
            todos = todosMainScreenViewModel.observeTodoList().collectAsState(initial = listOf()).value,
            getRemindersForTodo = { todo ->
                todosMainScreenViewModel
                    .observeRemindersForTodo(todo.id)
                    .collectAsState(initial = listOf())
                    .value
            },
            todoAdditionState = todosMainScreenViewModel.todoAdditionState.collectAsState().value,
            undoTodoRemovalState = undoTodoRemovalState,
            paddingValues,
            onCompleteAddTodo = {
                todosMainScreenViewModel.completeAddTodo(onNavigateToAddReminder)
            },
            onRemoveTodo = todosMainScreenViewModel::removeTodo,
            onAddReminder = todosMainScreenViewModel::addReminderForTodo,
            onEditReminder = todosMainScreenViewModel::editReminder,
        )},
        floatingActionButton = {
            FloatingActionButton(
                shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 50)),
                containerColor = MaterialTheme.colorScheme.secondary,
                onClick = {
                    if (isAddingTodo) {
                        todosMainScreenViewModel.cancelAddTodo()
                    } else {
                        todosMainScreenViewModel.onStartAddTodoClicked()
                    }
                },
            ) {
                Icon(
                    imageVector = if (isAddingTodo) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (isAddingTodo) stringResource(R.string.cancel) else stringResource(
                                            R.string.add),
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    )
}

@Composable
fun TodoMainScreenContent(
    todos: List<Todo>,
    getRemindersForTodo: @Composable (Todo) -> List<Reminder>,
    todoAdditionState: TodoAdditionState?,
    undoTodoRemovalState: UndoTodoRemovalState,
    paddingValues: PaddingValues,
    onCompleteAddTodo: () -> Unit,
    onRemoveTodo: (Long) -> Unit,
    onAddReminder: (Long) -> Unit,
    onEditReminder: (Reminder) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box {
            TodoList(
                todos,
                getRemindersForTodo = getRemindersForTodo,
                onRemoveTodo = onRemoveTodo,
                onAddReminder = onAddReminder,
                onEditReminder = onEditReminder,
                undoableTodoRemovalIds = undoTodoRemovalState.undoableTodoRemovals,
                paddingValues,
            )

            if (todoAdditionState != null) {
                AddTodo(
                    todoAdditionState,
                    onCompleteAddTodo,
                )
            }
        }
    }
}

@Composable
fun EmptyTodoList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Text(
            text = stringResource(R.string.nothing_to_do),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.nothing_to_do_suggestion),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun TodoList(
    todos: List<Todo>,
    getRemindersForTodo: @Composable (Todo) -> List<Reminder>,
    onRemoveTodo: (Long) -> Unit,
    onAddReminder: (Long) -> Unit,
    onEditReminder: (Reminder) -> Unit,
    undoableTodoRemovalIds: List<Long>,
    paddingValues: PaddingValues,
) {
    if (todos.isEmpty()) {
        EmptyTodoList()
        return
    }

    val filteredTodos = todos.filterNot { todo ->
        undoableTodoRemovalIds.contains(todo.id)
    }

    LazyColumn(
        modifier = Modifier
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            count = filteredTodos.size,
            key = { index -> filteredTodos[index].id },
        ) { index ->
            val todo = filteredTodos[index]
            val reminders = getRemindersForTodo(todo)

            TodoItem(
                todo,
                reminders,
                onRemoveTodo,
                onAddReminder,
                onEditReminder,
            )
        }
        item {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

@Composable
fun AddTodo(
    todoAdditionState: TodoAdditionState,
    onCompleteAddTodoClicked: () -> Unit,
) {
    val localFocusManager = LocalFocusManager.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            },
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            BoxWithConstraints {
                val asColumn = maxWidth < 480.dp
                val paddingValues = if (asColumn) PaddingValues(bottom = 72.dp) else PaddingValues(end = 72.dp)
                val nofDescriptionLines =
                    if (maxHeight < 320.dp) 3
                    else if (maxHeight < 480.dp) 4
                    else 5

                Card(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(paddingValues),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 8.dp,
                    ),
                ) {
                    if (asColumn) {
                        AddTodoAsColumn(
                            todoAdditionState,
                            nofDescriptionLines,
                            onCompleteAddTodoClicked,
                        )
                    } else {
                        AddTodoAsRow(
                            todoAdditionState,
                            nofDescriptionLines,
                            onCompleteAddTodoClicked,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddTodoAsColumn(
    todoAdditionState: TodoAdditionState,
    nofDescriptionLines: Int,
    onCompleteAddTodoClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
    ) {
        AddTodoTitleAndDescription(
            title = todoAdditionState.title,
            showTitleValidationError = todoAdditionState.showTitleEmptyValidationError,
            description = todoAdditionState.description,
            nofDescriptionLines,
            onTitleChanged = todoAdditionState.onTitleChanged,
            onDescriptionChanged = todoAdditionState.onDescriptionChanged,
        )
        Spacer(modifier = Modifier.height(8.dp))
        AddTodoOptions(
            addReminder = todoAdditionState.addReminder,
            onAddReminderChanged = todoAdditionState.onAddReminderChanged,
            onCompleteAddTodoClicked,
        )
    }
}

@Composable
fun AddTodoAsRow(
    todoAdditionState: TodoAdditionState,
    nofDescriptionLines: Int,
    onCompleteAddTodoClicked: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .weight(1F)
                .verticalScroll(rememberScrollState()),
        ) {
            AddTodoTitleAndDescription(
                title = todoAdditionState.title,
                showTitleValidationError = todoAdditionState.showTitleEmptyValidationError,
                description = todoAdditionState.description,
                nofDescriptionLines,
                onTitleChanged = todoAdditionState.onTitleChanged,
                onDescriptionChanged = todoAdditionState.onDescriptionChanged,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom,
        ) {
            AddTodoOptions(
                addReminder = todoAdditionState.addReminder,
                onAddReminderChanged = todoAdditionState.onAddReminderChanged,
                onCompleteAddTodoClicked,
            )
        }
    }
}

@Composable
fun AddTodoTitleAndDescription(
    title: String,
    showTitleValidationError: Boolean,
    description: String,
    nofDescriptionLines: Int,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
) {
    val titleNeededValidationError = stringResource(R.string.title_needed_error)

    TextField(
        value = title,
        onValueChange = { onTitleChanged(it) },
        isError = showTitleValidationError,
        label = { Text(stringResource(if (!showTitleValidationError) R.string.title else R.string.title_needed_error )) },
        trailingIcon = {
            if (showTitleValidationError) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = stringResource(R.string.validation_failed)
                )
            }
        },
        textStyle = MaterialTheme.typography.titleMedium,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { if (showTitleValidationError) error(titleNeededValidationError) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    )
    Spacer(modifier = Modifier.height(8.dp))
    TextField(
        value = description,
        onValueChange = { onDescriptionChanged(it) },
        label = {
            Text(stringResource(R.string.description))
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        minLines = nofDescriptionLines,
        maxLines = nofDescriptionLines,
        modifier = Modifier
            .fillMaxWidth(),
    )
}

@Composable
fun ColumnScope.AddTodoOptions(
    addReminder: Boolean,
    onAddReminderChanged: (Boolean) -> Unit,
    onCompleteAddTodoClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = addReminder,
            onCheckedChange = { onAddReminderChanged(!addReminder) }
        )
        Text(
            text = stringResource(R.string.add_reminder),
            style = MaterialTheme.typography.titleMedium,
        )
    }
    Button(
        modifier = Modifier
            .align(Alignment.End),
        onClick = onCompleteAddTodoClicked,
    ) {
        Text(stringResource(R.string.add))
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun EmptyTodoListPreview() {
    DesastreTheme {
        EmptyTodoList()
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun TodoListPreview() {
    DesastreTheme {
        TodoList(
            todos = PreviewUtil.mockTodos(
                size = 100
            ),
            getRemindersForTodo = { todo ->
                if (todo.id % 2 == 0L) {
                    val tomorrow = LocalDateTime.now().plusDays(1)
                    listOf(
                        Reminder(
                            id = Random.nextLong(),
                            todoId = todo.id,
                            minute = 15,
                            hour = (todo.id.toInt() % 23L).toInt(),
                            day = tomorrow.dayOfMonth,
                            month = tomorrow.monthValue,
                            year = tomorrow.year
                        )
                    )
                } else {
                    listOf()
                }
            },
            onRemoveTodo = {},
            onAddReminder = {},
            onEditReminder = {},
            undoableTodoRemovalIds = listOf(),
            paddingValues = PaddingValues(8.dp),
        )
    }
}

@Preview(name="tiny-square", showBackground = true, widthDp = 240, heightDp = 240)
@Preview(name="small-square", showBackground = true, widthDp = 320, heightDp = 320)
@Preview(name="medium-column", showBackground = true, widthDp = 480, heightDp = 400)
@Preview(name="medium-row", showBackground = true, widthDp = 640, heightDp = 400)
@Composable
fun AddTodoPreview() {
    DesastreTheme {
        AddTodo(
            TodoAdditionState(
                title = "Title",
                showTitleEmptyValidationError = true,
                description = "Description",
                addReminder = true,
                onTitleChanged = {},
                onDescriptionChanged = {},
                onAddReminderChanged = {},
            ),
            onCompleteAddTodoClicked = {},
        )
    }
}