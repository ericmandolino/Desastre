package com.swirlfist.desastre.data.model

import java.time.LocalDateTime

data class Reminder(
    val id: Long = 0,
    val todoId: Long,
    val time: LocalDateTime,
)
