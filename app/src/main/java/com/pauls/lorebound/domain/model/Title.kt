package com.pauls.lorebound.domain.model

data class Title(
    val id: String,
    val name: String,
    val description: String,
    val category: QuestCategory,
    val requiredQuestCount: Int,
    val requiredCategory: QuestCategory? = null,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

