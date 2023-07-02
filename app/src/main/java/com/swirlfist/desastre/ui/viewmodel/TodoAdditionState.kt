package com.swirlfist.desastre.ui.viewmodel

data class TodoAdditionState(
    val title: String,
    val showTitleEmptyValidationError: Boolean,
    val description: String,
    val addReminder: Boolean,
    val onTitleChanged: (String) -> Unit,
    val onDescriptionChanged: (String) -> Unit,
    val onAddReminderChanged: (Boolean) -> Unit,
)
