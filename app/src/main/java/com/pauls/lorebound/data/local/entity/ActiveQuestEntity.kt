package com.pauls.lorebound.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_quests")
data class ActiveQuestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val questId: Long,
    val questType: String,
    val assignedDate: String,
    val expiresDate: String,
    val isCompleted: Boolean = false,
    val completedDate: String? = null
)

