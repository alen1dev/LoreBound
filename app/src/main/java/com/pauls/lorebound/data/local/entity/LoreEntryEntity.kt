package com.pauls.lorebound.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lore_entries")
data class LoreEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val questId: Long = 0L,
    val completedQuestId: Long = 0L,
    val date: String,
    val questTitle: String,
    val xpEarned: Int = 0,
    val traitsImproved: String = "",
    val userNotes: String? = null,
    val photoUri: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String? = null,
    val rankAtCompletion: Int = 0,
    // Personal lore fields
    val isPersonal: Boolean = false,
    val tags: String = "",
    val isFavorite: Boolean = false,
    val storyWeight: Int = 0
)

