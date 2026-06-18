package com.pauls.lorebound.domain.model

data class DailyQuest(
    val id: Long = 0L,
    val questId: Long,
    val date: String,
    val isCompleted: Boolean
)

