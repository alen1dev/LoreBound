package com.pauls.lorebound.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val slug: String,
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
    val verificationType: String,
    val tags: String = ""
)

