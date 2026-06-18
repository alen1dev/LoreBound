package com.pauls.lorebound.domain.repository

import com.pauls.lorebound.domain.model.CompletedQuest
import com.pauls.lorebound.domain.model.LoreEntry
import kotlinx.coroutines.flow.Flow

interface LoreRepository {
    fun getAllLoreEntries(): Flow<List<LoreEntry>>
    fun getLoreEntriesByDate(date: String): Flow<List<LoreEntry>>
    fun getPersonalEntries(): Flow<List<LoreEntry>>
    fun getQuestEntries(): Flow<List<LoreEntry>>
    fun getFavoriteEntries(): Flow<List<LoreEntry>>
    suspend fun getLoreEntryById(id: Long): LoreEntry?
    suspend fun insertLoreEntry(loreEntry: LoreEntry): Long
    suspend fun updateLoreEntry(loreEntry: LoreEntry)
    suspend fun deleteLoreEntry(id: Long)
    suspend fun getLoreEntryCount(): Int
    fun searchLoreEntries(query: String): Flow<List<LoreEntry>>

    // Completed quests
    suspend fun insertCompletedQuest(completedQuest: CompletedQuest): Long
    suspend fun getCompletedQuestCount(): Int
    suspend fun getCompletedQuestCountByCategory(category: String): Int
    suspend fun getCompletedQuestCountByAttribute(attribute: String): Int
    suspend fun getDistinctLocationCount(): Int
    suspend fun deleteAllLoreEntries()
    suspend fun deleteAllCompletedQuests()
    suspend fun getAllCompletedQuests(): List<CompletedQuest>
    suspend fun getAllLoreEntriesList(): List<LoreEntry>
}

