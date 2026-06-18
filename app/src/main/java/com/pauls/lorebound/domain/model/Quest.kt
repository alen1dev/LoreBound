package com.pauls.lorebound.domain.model

data class Quest(
    val id: Long = 0L,
    val slug: String,
    val title: String,
    val description: String,
    val questType: QuestType,
    val primaryAttribute: Trait,
    val secondaryAttribute: Trait? = null,
    val xpReward: Int,
    val difficulty: Int,
    val estimatedMinutes: Int,
    val durationDays: Int = 1,
    val storyWeight: Int = 1,
    val rarity: Rarity,
    val verificationType: VerificationType,
    val tags: List<String> = emptyList()
)

