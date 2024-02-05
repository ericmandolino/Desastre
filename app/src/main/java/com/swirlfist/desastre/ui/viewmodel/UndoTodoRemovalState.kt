package com.swirlfist.desastre.ui.viewmodel

data class UndoTodoRemovalState(
    val undoableTodoRemovals: List<Long> = listOf()
)
