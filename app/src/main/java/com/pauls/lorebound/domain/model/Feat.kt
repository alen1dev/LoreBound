package com.pauls.lorebound.domain.model

data class Feat(
    val id: String,
    val name: String,
    val description: String,
    val requirement: FeatRequirement,
    val targetValue: Int,
    val currentValue: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

enum class FeatRequirement(val displayName: String) {
    QUESTS_COMPLETED("Quests Conquered"),
    STREAK_DAYS("Day Streak"),
    RANK_REACHED("Rank Reached"),
    LOCATIONS_VISITED("Locations Visited"),
    CATEGORY_QUESTS_COMPLETED("Category Quests Conquered")
}

