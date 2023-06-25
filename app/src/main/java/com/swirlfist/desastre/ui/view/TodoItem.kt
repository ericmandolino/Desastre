package com.swirlfist.desastre.ui.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swirlfist.desastre.R
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.ui.theme.DesastreTheme

@Composable
fun TodoItem(
    todo: Todo,
    onRemove: (Long) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
            Text(
                text = todo.description,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun UndoTodoItemRemoval(
    todoId: Long,
    removalCountdownProgress: Int,
    onUndoClicked: (Long) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable { onUndoClicked(todoId) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            Text(
                text = stringResource(R.string.undo),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
            )
            Row {
                val undoAvailabilityProgress = removalCountdownProgress.toFloat() / 100
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(maxOf(undoAvailabilityProgress, 0.01F))
                        .background(Color.Black.copy(alpha = 0.1f)),
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(maxOf(1F - undoAvailabilityProgress, 0.01F))
                        .background(Color.Transparent),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun TodoItemPreview() {
    DesastreTheme {
        TodoItem(
            todo = PreviewUtil.mockTodo(
                title = "Title",
                description = "Aliquid facilis aperiam itaque et cumque sed totam est. Esse soluta modi perspiciatis. Placeat quis cum et enim. Quia reiciendis reprehenderit atque. Ea quaerat id nihil repudiandae. Et tenetur consectetur ad ipsa quia.",
                isDone = false,
            ),
            onRemove = {},
        )
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    widthDp = 320,
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    widthDp = 320,
)
@Composable
fun UndoTodoItemRemovalPreview() {
    DesastreTheme {
        UndoTodoItemRemoval(
            todoId = 1L,
            removalCountdownProgress = 50,
            onUndoClicked = {},
        )
    }
}