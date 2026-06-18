package com.pauls.lorebound.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "titles")
data class TitleEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val requiredQuestCount: Int,
    val requiredCategory: String? = null,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

