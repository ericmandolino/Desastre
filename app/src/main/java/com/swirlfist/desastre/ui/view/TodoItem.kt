package com.swirlfist.desastre.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.ui.theme.DesastreTheme

@Composable
fun TodoItem(
    todo: Todo,
    reminders: List<Reminder>,
    onClick: (todoId: Long) -> Unit,
    onRemove: (Long) -> Unit,
    onAddReminder: (Long) -> Unit,
    onEditReminder: (Reminder) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(todo.id) },
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                IconButton(
                    onClick = { onRemove(todo.id) },
                    modifier = Modifier
                        .align(Alignment.Top),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Remove",
                    )
                }
            }
            if (todo.description.isNotEmpty()) {
                Text(
                    text = todo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Reminders(
                reminders = reminders,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                onAddReminder = { onAddReminder(todo.id) },
                onEditReminder = onEditReminder,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun TodoItemNoDescriptionPreview() {
    DesastreTheme {
        TodoItem(
            todo = PreviewUtil.mockTodo(
                title = PreviewUtil.mockTitle(),
                description = "",
                isDone = false,
            ),
            reminders = PreviewUtil.mockReminders(4),
            onClick = {},
            onRemove = {},
            onAddReminder = {},
            onEditReminder = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun TodoItemLongDescriptionPreview() {
    DesastreTheme {
        TodoItem(
            todo = PreviewUtil.mockTodo(
                title = PreviewUtil.mockTitle(),
                description = PreviewUtil.mockDescription(),
                isDone = false,
            ),
            reminders = PreviewUtil.mockReminders(4),
            onClick = {},
            onRemove = {},
            onAddReminder = {},
            onEditReminder = {},
        )
    }
}