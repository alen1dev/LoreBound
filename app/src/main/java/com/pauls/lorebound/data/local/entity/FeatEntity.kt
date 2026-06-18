package com.pauls.lorebound.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feats")
data class FeatEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val requirement: String,
    val targetValue: Int,
    val currentValue: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

