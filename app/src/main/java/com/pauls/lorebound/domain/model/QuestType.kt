package com.pauls.lorebound.domain.model

enum class QuestType(
    val displayName: String,
    val defaultDurationDays: Int,
    val refreshLabel: String
) {
    DAILY("Daily Quest", 1, "Refreshes daily"),
    SIDE_QUEST("Side Quest", 7, "Refreshes weekly"),
    ADVENTURE("Adventure", 30, "Refreshes monthly"),
    EPIC("Epic", 90, "Refreshes quarterly")
}

