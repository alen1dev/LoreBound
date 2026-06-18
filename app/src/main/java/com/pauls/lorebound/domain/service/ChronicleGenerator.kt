package com.pauls.lorebound.domain.service

import com.pauls.lorebound.domain.model.LoreEntry

interface ChronicleGenerator {
    suspend fun generateYearlyChronicle(year: Int, entries: List<LoreEntry>): Chronicle
}

data class Chronicle(
    val year: Int,
    val adventuresCompleted: Int,
    val titlesEarned: Int,
    val locationsVisited: Int,
    val mostImprovedTrait: String,
    val totalXpEarned: Long,
    val summary: String
)

