package com.pauls.lorebound.domain.model

data class CompletedQuest(
    val id: Long = 0L,
    val questId: Long,
    val completedAt: Long = System.currentTimeMillis(),
    val verificationType: VerificationType,
    val photoUri: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

