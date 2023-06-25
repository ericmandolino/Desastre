package com.swirlfist.desastre.ui.viewmodel

data class UndoableTodoRemovalState(
    val undoableTodoRemovals: Map<Long, Int>,
    val onUndoClicked: (Long) -> Unit,
)
