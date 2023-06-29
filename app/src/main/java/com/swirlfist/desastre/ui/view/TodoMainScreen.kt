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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.swirlfist.desastre.R
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.ui.theme.DesastreTheme
import com.swirlfist.desastre.ui.viewmodel.TodosMainScreenViewModel
import com.swirlfist.desastre.ui.viewmodel.UndoableTodoRemovalState

const val TITLE_MAX_CHARACTERS = 50
const val DESCRIPTION_MAX_CHARACTERS = 2000

@Composable
fun TodoMainScreen(
    todosMainScreenViewModel: TodosMainScreenViewModel = hiltViewModel(),
) {
    var isAddingTodo by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.padding(8.dp),
        content = { paddingValues ->  TodoMainScreenContent(
            todosMainScreenViewModel,
            isAddingTodo,
            paddingValues,
        )},
        floatingActionButton = {
            FloatingActionButton(
                shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 50)),
                containerColor = MaterialTheme.colorScheme.secondary,
                onClick = {
                    isAddingTodo = !isAddingTodo
                },
            ) {
                Icon(
                    imageVector = if (isAddingTodo) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (isAddingTodo) stringResource(R.string.cancel) else stringResource(
                                            R.string.add),
                )
            }
        },
    )
}

@Composable
fun TodoMainScreenContent(
    todosMainScreenViewModel: TodosMainScreenViewModel,
    isAddingTodo: Boolean,
    paddingValues: PaddingValues,
) {
    val todos = todosMainScreenViewModel.getTodoList().collectAsState(initial = listOf()).value
    val undoableRemovalState = todosMainScreenViewModel.getUndoableRemovalState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box {
            if (todos.isEmpty()) {
                // TODO: show empty component
            } else {
                TodoList(
                    todos,
                    onRemoveTodo = { todoId ->
                        todosMainScreenViewModel.removeTodo(todoId)
                    },
                    undoableRemovalState,
                    paddingValues,
                )
            }
            if (isAddingTodo) {
                AddTodo()
            }
        }
    }
}

@Composable
fun TodoList(
    todos: List<Todo>,
    onRemoveTodo: (Long) -> Unit,
    undoableRemovalState: UndoableTodoRemovalState,
    paddingValues: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            count = todos.size,
            key = { index -> todos[index].id },
        ) { index ->
            val todo = todos[index]
            if (undoableRemovalState.undoableTodoRemovals.containsKey(todo.id)) {
                UndoTodoItemRemoval(
                    todo,
                    removalCountdownProgress = undoableRemovalState.undoableTodoRemovals[todo.id]?: 0,
                    onUndoClicked = undoableRemovalState.onUndoClicked,
                )
            } else {
                TodoItem(
                    todo,
                    onRemoveTodo,
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

@Composable
fun AddTodo() {
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
                    var title by rememberSaveable { mutableStateOf("") }
                    var description by rememberSaveable { mutableStateOf("") }
                    val onTitleChanged: (String) -> Unit = { it -> title = if (it.length > TITLE_MAX_CHARACTERS) it.substring(0, TITLE_MAX_CHARACTERS) else it }
                    val onDescriptionChanged: (String) -> Unit = { description = if (it.length > DESCRIPTION_MAX_CHARACTERS) it.substring(0, DESCRIPTION_MAX_CHARACTERS) else it }

                    if (asColumn) {
                        AddTodoAsColumn(
                            title,
                            description,
                            nofDescriptionLines,
                            onTitleChanged,
                            onDescriptionChanged,
                        )
                    } else {
                        AddTodoAsRow(
                            title,
                            description,
                            nofDescriptionLines,
                            onTitleChanged,
                            onDescriptionChanged,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddTodoAsColumn(
    title: String,
    description: String,
    nofDescriptionLines: Int,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
    ) {
        AddTodoTitleAndDescription(
            title,
            description,
            nofDescriptionLines,
            onTitleChanged,
            onDescriptionChanged,
        )
        Spacer(modifier = Modifier.height(8.dp))
        AddTodoOptions()
    }
}

@Composable
fun AddTodoAsRow(
    title: String,
    description: String,
    nofDescriptionLines: Int,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
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
                title,
                description,
                nofDescriptionLines,
                onTitleChanged,
                onDescriptionChanged,
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
            AddTodoOptions()
        }
    }
}

@Composable
fun AddTodoTitleAndDescription(
    title: String,
    description: String,
    nofDescriptionLines: Int,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
) {
    TextField(
        value = title,
        onValueChange = { onTitleChanged(it) },
        label = {
            Text(stringResource(R.string.title))
        },
        textStyle = MaterialTheme.typography.titleMedium,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth(),
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
fun ColumnScope.AddTodoOptions() {
    var addReminderChecked by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = addReminderChecked,
            onCheckedChange = {addReminderChecked = !addReminderChecked }
        )
        Text(
            text = stringResource(R.string.add_reminder),
            style = MaterialTheme.typography.titleMedium,
        )
    }
    Button(
        modifier = Modifier
            .align(Alignment.End),
        onClick = { /*TODO*/ },
    ) {
        Text(stringResource(R.string.add))
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
            onRemoveTodo = {},
            undoableRemovalState = UndoableTodoRemovalState(
                undoableTodoRemovals = mapOf(),
                onUndoClicked = {},
            ),
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
        AddTodo()
    }
}