package com.pauls.lorebound.domain.model

data class LoreEntry(
    val id: Long = 0L,
    val questId: Long = 0L,
    val completedQuestId: Long = 0L,
    val date: String,
    val questTitle: String,
    val xpEarned: Int = 0,
    val traitsImproved: List<Trait> = emptyList(),
    val userNotes: String? = null,
    val photoUri: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String? = null,
    val rankAtCompletion: Int = 0,
    // Personal lore fields
    val isPersonal: Boolean = false,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val storyWeight: Int = 0
) {
    /** Whether this entry originated from a quest */
    val isQuestEntry: Boolean get() = !isPersonal

    /** Display title — questTitle for quest entries, questTitle field used as personal title */
    val displayTitle: String get() = questTitle
}

