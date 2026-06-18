package com.pauls.lorebound.data.seed

import kotlinx.serialization.Serializable

/**
 * JSON-serializable quest DTO.
 * Supports both legacy flat format and new nested verification format.
 *
 * Legacy: "verificationType": "PHOTO"
 * New:    "verification": { "type": "PHOTO_AND_TEXT" }
 */
@Serializable
data class QuestJson(
    val id: String,
    val title: String,
    val description: String,
    val questType: String,
    val primaryAttribute: String,
    val secondaryAttribute: String? = null,
    val xpReward: Int,
    val difficulty: Int,
    val estimatedMinutes: Int,
    val durationDays: Int = 1,
    val storyWeight: Int = 1,
    val rarity: String,
    // New nested verification object (preferred)
    val verification: VerificationJson? = null,
    // Legacy flat field (backward compatible)
    val verificationType: String? = null,
    val tags: List<String> = emptyList()
)

@Serializable
data class VerificationJson(
    val type: String
)

