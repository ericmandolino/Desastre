package com.swirlfist.desastre.ui.viewmodel

private const val TITLE_MAX_CHARACTERS = 50

data class TodoTitleInputState(
    val titleText: String = "",
    val titleValidation: TodoTitleValidationResult = TodoTitleValidationResult.SUCCESS,
    val onTitleValueChanged: (String) -> Unit = {},
)

fun TodoTitleInputState.updateTitleText(title: String) = this.copy(
    titleText = if (title.length > TITLE_MAX_CHARACTERS) title.substring(
        0,
        TITLE_MAX_CHARACTERS
    ) else title,
    titleValidation = TodoTitleValidationResult.SUCCESS,
)

fun TodoTitleInputState.validateTitleText() = this.copy(
    titleValidation = if (this.titleText.isEmpty()) {
        TodoTitleValidationResult.TITLE_CANNOT_BE_EMPTY
    } else {
        TodoTitleValidationResult.SUCCESS
    }
)

enum class TodoTitleValidationResult {
    SUCCESS,
    TITLE_CANNOT_BE_EMPTY
}
