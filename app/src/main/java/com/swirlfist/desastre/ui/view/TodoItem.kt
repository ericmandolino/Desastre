package com.swirlfist.desastre.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swirlfist.desastre.R
import com.swirlfist.desastre.data.model.Reminder
import com.swirlfist.desastre.data.model.Todo
import com.swirlfist.desastre.ui.theme.DesastreTheme
import com.swirlfist.desastre.util.isForToday
import com.swirlfist.desastre.util.isForTomorrow
import com.swirlfist.desastre.util.isInThePast
import com.swirlfist.desastre.util.toShortFormat
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun TodoItem(
    todo: Todo,
    reminders: List<Reminder>,
    onRemove: (Long) -> Unit,
    onAddReminder: (Long) -> Unit,
    onEditReminder: (Reminder) -> Unit,
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
            if (todo.description.isNotEmpty()) {
                Text(
                    text = todo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            TodoReminders(
                reminders = reminders,
                onAddReminder = { onAddReminder(todo.id) },
                onEditReminder = onEditReminder,
            )
        }
    }
}

@Composable
fun TodoReminders(
    reminders: List<Reminder>,
    onAddReminder: () -> Unit,
    onEditReminder: (Reminder) -> Unit,
) {
    LazyRow(
        userScrollEnabled = true,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            AddOrEditReminderButton(
                text = "+",
                strikeText = false,
                onClick = onAddReminder,
            )
        }
        items(reminders) { reminder ->
            ReminderItem(reminder, onEditReminder)
        }
    }
}

@Composable
fun ReminderItem(
    reminder: Reminder,
    onReminderClick: (Reminder) -> Unit,
) {

    val dateText = if (reminder.isForToday()) {
        stringResource(R.string.today)
    } else if (reminder.isForTomorrow()) {
        stringResource(id = R.string.tomorrow)
    } else {
        LocalDate.of(reminder.year, reminder.month, reminder.day).toShortFormat()
    }

    val timeText = LocalTime.of(reminder.hour, reminder.minute).toShortFormat()

    AddOrEditReminderButton(
        text = "$dateText @ $timeText",
        strikeText = reminder.isInThePast(),
        onClick = { onReminderClick(reminder) },
    )
}

@Composable
fun AddOrEditReminderButton(
    text: String,
    strikeText: Boolean = false,
    onClick: () -> Unit,
) {
    Button(onClick = onClick) {
        Text(
            text = text,
            style = (
                if (strikeText) {
                    MaterialTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.LineThrough
                    )
                } else {
                    MaterialTheme.typography.bodySmall
                }
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun TodoItemNoDescriptionPreview() {
    DesastreTheme {
        TodoItem(
            todo = PreviewUtil.mockTodo(
                title = "Aliquid facilis aperiam itaque et cumque sed totam est.",
                description = "",
                isDone = false,
            ),
            reminders = PreviewUtil.mockReminders(4),
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
                title = "Aliquid facilis aperiam itaque et cumque sed totam est.",
                description = "Aliquid facilis aperiam itaque et cumque sed totam est. Esse soluta modi perspiciatis. Placeat quis cum et enim. Quia reiciendis reprehenderit atque. Ea quaerat id nihil repudiandae. Et tenetur consectetur ad ipsa quia.",
                isDone = false,
            ),
            reminders = PreviewUtil.mockReminders(4),
            onRemove = {},
            onAddReminder = {},
            onEditReminder = {},
        )
    }
}

@Preview
@Composable
fun ReminderItemPreview() {
    DesastreTheme {
        ReminderItem(PreviewUtil.mockReminder()) {}
    }
}

@Preview
@Composable
fun ReminderItemTodayPreview() {
    DesastreTheme {
        ReminderItem(PreviewUtil.mockTodayReminder()) {}
    }
}

@Preview
@Composable
fun ReminderItemTomorrowPreview() {
    DesastreTheme {
        ReminderItem(PreviewUtil.mockTomorrowReminder()) {}
    }
}

@Preview
@Composable
fun ReminderItemPastPreview() {
    DesastreTheme {
        ReminderItem(PreviewUtil.mockYesterdayReminder()) {}
    }
}