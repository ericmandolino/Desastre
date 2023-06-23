package com.swirlfist.desastre.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.ui.theme.DesastreTheme
import com.swirlfist.desastre.ui.viewmodel.TodosMainScreenViewModel
import com.swirlfist.desastre.ui.viewmodel.UndoableTodoRemovalState

@Composable
fun TodoMainScreen(
    todosMainScreenViewModel: TodosMainScreenViewModel = hiltViewModel(),
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
        )
    }
}

@Composable
fun TodoList(
    todos: List<Todo>,
    onRemoveTodo: (Long) -> Unit,
    undoableRemovalState: UndoableTodoRemovalState,
) {
    LazyColumn(
        modifier = Modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            count = todos.size,
            key = { index -> todos[index].id },
        ) { index ->
            val todo = todos[index]
            if (undoableRemovalState.undoableTodoRemovalIds.contains(todo.id)) {
                UndoTodoItemRemoval(
                    todoId = todo.id,
                    onUndoClicked = undoableRemovalState.onUndoClicked,
                )
            } else {
                TodoItem(
                    todo,
                    onRemoveTodo,
                )
            }
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
                undoableTodoRemovalIds = listOf(),
                onUndoClicked = {},
            )
        )
    }
}