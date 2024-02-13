package com.swirlfist.desastre.ui.viewmodel

private const val DESCRIPTION_MAX_CHARACTERS = 2000

data class TodoDescriptionInputState(
    val descriptionText: String = "",
    val onDescriptionValueChanged: (String) -> Unit = {},
)

fun TodoDescriptionInputState.updateDescriptionText(
    description: String
): TodoDescriptionInputState = this.copy(
    descriptionText = if (description.length > DESCRIPTION_MAX_CHARACTERS) description.substring(
        0,
        DESCRIPTION_MAX_CHARACTERS
    ) else description
)
