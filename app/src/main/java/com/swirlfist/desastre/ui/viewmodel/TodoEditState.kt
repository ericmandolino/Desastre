package com.swirlfist.desastre.ui.viewmodel

data class TodoEditState(
    val isEditing: Boolean = false,
    val titleInputState: TodoTitleInputState = TodoTitleInputState(),
    val descriptionInputState: TodoDescriptionInputState = TodoDescriptionInputState(),
)
