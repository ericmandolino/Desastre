package com.swirlfist.desastre.ui.viewmodel

data class UndoableTodoRemovalState(
    val undoableTodoRemovalIds: List<Long>,
    val onUndoClicked: (Long) -> Unit,
)
