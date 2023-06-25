package com.swirlfist.desastre.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.swirlfist.desastre.R
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.ui.theme.DesastreTheme
import com.swirlfist.desastre.ui.viewmodel.TodosMainScreenViewModel
import com.swirlfist.desastre.ui.viewmodel.UndoableTodoRemovalState

@Composable
fun TodoMainScreen(
    todosMainScreenViewModel: TodosMainScreenViewModel = hiltViewModel(),
) {
    Scaffold(
        modifier = Modifier.padding(8.dp),
        content = { paddingValues ->  TodoMainScreenContent(
            todosMainScreenViewModel, paddingValues
        )},
        floatingActionButton = {
            FloatingActionButton(
                shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 50)),
                containerColor = MaterialTheme.colorScheme.secondary,
                onClick = { },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        },
    )

}

@Composable
fun TodoMainScreenContent(
    todosMainScreenViewModel: TodosMainScreenViewModel,
    paddingValues: PaddingValues,
) {
    val todos = todosMainScreenViewModel.getTodoList().collectAsState(initial = listOf()).value
    val undoableRemovalState = todosMainScreenViewModel.getUndoableRemovalState()

    if (todos.isEmpty()) {
        return // TODO: show empty component
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        TodoList(
            todos,
            onRemoveTodo = { todoId ->
                todosMainScreenViewModel.removeTodo(todoId)
            },
            undoableRemovalState,
            paddingValues,
        )
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