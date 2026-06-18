package com.pauls.lorebound.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val strength: Int,
    val intelligence: Int,
    val charisma: Int,
    val creativity: Int,
    val exploration: Int,
    val courage: Int,
    val totalXp: Long = 0L,
    val currentTitle: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: String? = null
)
