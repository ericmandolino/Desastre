package com.swirlfist.desastre.data.model

data class Todo(
    val id: Long = 0,
    val title: String,
    val description: String,
    val isDone: Boolean,
)
