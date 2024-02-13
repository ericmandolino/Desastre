package com.swirlfist.desastre.ui.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.swirlfist.desastre.R
import com.swirlfist.desastre.ui.theme.DesastreTheme
import com.swirlfist.desastre.ui.viewmodel.TodoDescriptionInputState
import com.swirlfist.desastre.ui.viewmodel.TodoTitleInputState
import com.swirlfist.desastre.ui.viewmodel.TodoTitleValidationResult

@Composable
fun TodoNotFound() {
    NotFound()
}

@Composable
fun TodoTitleInput(
    todoTitleInputState: TodoTitleInputState,
) {
    val titleNeededValidationError = stringResource(R.string.title_needed_error)
    val isValidationError = todoTitleInputState.titleValidation != TodoTitleValidationResult.SUCCESS
    val labelText = stringResource(
        when (todoTitleInputState.titleValidation) {
            TodoTitleValidationResult.SUCCESS -> R.string.title
            TodoTitleValidationResult.TITLE_CANNOT_BE_EMPTY -> R.string.title_needed_error
        }
    )

    TextField(
        value = todoTitleInputState.titleText,
        onValueChange = { todoTitleInputState.onTitleValueChanged(it) },
        isError = isValidationError,
        label = { Text(labelText) },
        trailingIcon = {
            if (isValidationError) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = stringResource(R.string.validation_failed)
                )
            }
        },
        textStyle = MaterialTheme.typography.titleMedium,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                when (todoTitleInputState.titleValidation) {
                    TodoTitleValidationResult.SUCCESS -> Unit
                    TodoTitleValidationResult.TITLE_CANNOT_BE_EMPTY -> error(titleNeededValidationError)
                }
            },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    )
}

@Composable
fun TodoDescriptionInput(
    modifier: Modifier = Modifier,
    todoDescriptionInputState: TodoDescriptionInputState,
    nofDescriptionLines: Int,
) {
    TextField(
        value = todoDescriptionInputState.descriptionText,
        onValueChange = { todoDescriptionInputState.onDescriptionValueChanged(it) },
        label = {
            Text(stringResource(R.string.description))
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        minLines = nofDescriptionLines,
        maxLines = nofDescriptionLines,
        modifier = modifier
            .fillMaxWidth(),
    )
}

@Preview
@Composable
fun TodoTitleInputPreview() {
    DesastreTheme {
        TodoTitleInput(
            todoTitleInputState = TodoTitleInputState(
                titleText = PreviewUtil.mockTitle(),
            )
        )
    }
}

@Preview
@Composable
fun TodoTitleInputErrorPreview() {
    DesastreTheme {
        TodoTitleInput(
            todoTitleInputState = TodoTitleInputState(
                titleValidation = TodoTitleValidationResult.TITLE_CANNOT_BE_EMPTY,
            )
        )
    }
}

@Preview
@Composable
fun TodoDescriptionInputPreview() {
    DesastreTheme {
        TodoDescriptionInput(
            todoDescriptionInputState = TodoDescriptionInputState(
                descriptionText = PreviewUtil.mockDescription(),
            ),
            nofDescriptionLines = 3,
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun TodoNotFoundPreview() {
    DesastreTheme {
        TodoNotFound()
    }
}