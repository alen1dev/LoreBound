package com.pauls.lorebound.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_quests")
data class CompletedQuestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val questId: Long,
    val completedAt: Long = System.currentTimeMillis(),
    val verificationType: String,
    val photoUri: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

