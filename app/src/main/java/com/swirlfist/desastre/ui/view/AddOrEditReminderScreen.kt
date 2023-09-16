package com.swirlfist.desastre.ui.view

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.swirlfist.desastre.ui.viewmodel.AddOrEditReminderScreenViewModel

@Composable
fun AddOrEditReminderScreen(
    addOrEditReminderScreenViewModel: AddOrEditReminderScreenViewModel = hiltViewModel(),
    todoId: Long?,
    reminderId: Long?,
) {
    val todo = if (todoId != null) addOrEditReminderScreenViewModel.observeTodo(todoId).collectAsState(initial = null).value else null
    Log.e("gus", "add/edit reminder for '${todo?.title}', id: $todoId")
    DaySelector()
}