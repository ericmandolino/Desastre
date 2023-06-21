package com.swirlfist.desastre.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.ui.theme.DesastreTheme

@Composable
fun TodoItem(todo: Todo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = todo.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = todo.description,
                style = MaterialTheme.typography.bodyMedium,
            )
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
            )
        )
    }
}