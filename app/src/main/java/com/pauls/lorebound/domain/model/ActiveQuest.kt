package com.pauls.lorebound.domain.model

data class ActiveQuest(
    val id: Long = 0L,
    val questId: Long,
    val questType: QuestType,
    val assignedDate: String,
    val expiresDate: String,
    val isCompleted: Boolean = false,
    val completedDate: String? = null
)

