package com.swirlfist.desastre.ui.viewmodel

data class TodoAdditionState(
    val titleInputState: TodoTitleInputState = TodoTitleInputState(),
    val descriptionInputState: TodoDescriptionInputState = TodoDescriptionInputState(),
    val addReminder: Boolean = false,
    val onAddReminderChanged: (Boolean) -> Unit = {},
)
