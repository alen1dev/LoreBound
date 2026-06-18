package com.pauls.lorebound.data.repository

import com.pauls.lorebound.data.local.dao.LoreEntryDao
import com.pauls.lorebound.data.local.mapper.toDomain
import com.pauls.lorebound.data.local.mapper.toEntity
import com.pauls.lorebound.domain.model.CompletedQuest
import com.pauls.lorebound.domain.model.LoreEntry
import com.pauls.lorebound.domain.repository.LoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoreRepositoryImpl @Inject constructor(
    private val loreEntryDao: LoreEntryDao
) : LoreRepository {

    override fun getAllLoreEntries(): Flow<List<LoreEntry>> =
        loreEntryDao.getAllLoreEntries().map { entities -> entities.map { it.toDomain() } }

    override fun getLoreEntriesByDate(date: String): Flow<List<LoreEntry>> =
        loreEntryDao.getLoreEntriesByDate(date).map { entities -> entities.map { it.toDomain() } }

    override fun getPersonalEntries(): Flow<List<LoreEntry>> =
        loreEntryDao.getPersonalEntries().map { entities -> entities.map { it.toDomain() } }

    override fun getQuestEntries(): Flow<List<LoreEntry>> =
        loreEntryDao.getQuestEntries().map { entities -> entities.map { it.toDomain() } }

    override fun getFavoriteEntries(): Flow<List<LoreEntry>> =
        loreEntryDao.getFavoriteEntries().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getLoreEntryById(id: Long): LoreEntry? =
        loreEntryDao.getLoreEntryById(id)?.toDomain()

    override suspend fun insertLoreEntry(loreEntry: LoreEntry): Long =
        loreEntryDao.insert(loreEntry.toEntity())

    override suspend fun updateLoreEntry(loreEntry: LoreEntry) =
        loreEntryDao.update(loreEntry.toEntity())

    override suspend fun deleteLoreEntry(id: Long) =
        loreEntryDao.deleteById(id)

    override suspend fun getLoreEntryCount(): Int =
        loreEntryDao.getCount()

    override fun searchLoreEntries(query: String): Flow<List<LoreEntry>> =
        loreEntryDao.search(query).map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertCompletedQuest(completedQuest: CompletedQuest): Long =
        loreEntryDao.insertCompletedQuest(completedQuest.toEntity())

    override suspend fun getCompletedQuestCount(): Int =
        loreEntryDao.getCompletedQuestCount()

    override suspend fun getCompletedQuestCountByCategory(category: String): Int {
        // Map legacy category to attribute for query compatibility
        val attribute = when (category) {
            "EXPLORATION" -> "EXPLORATION"
            "LEARNING" -> "INTELLIGENCE"
            "FITNESS" -> "STRENGTH"
            "CREATIVITY" -> "CREATIVITY"
            "SOCIAL" -> "CHARISMA"
            "ADVENTURE" -> "EXPLORATION"
            "COURAGE" -> "COURAGE"
            else -> category
        }
        return loreEntryDao.getCompletedQuestCountByAttribute(attribute)
    }

    override suspend fun getCompletedQuestCountByAttribute(attribute: String): Int =
        loreEntryDao.getCompletedQuestCountByAttribute(attribute)

    override suspend fun getDistinctLocationCount(): Int =
        loreEntryDao.getDistinctLocationCount()

    override suspend fun deleteAllLoreEntries() =
        loreEntryDao.deleteAll()

    override suspend fun deleteAllCompletedQuests() =
        loreEntryDao.deleteAllCompletedQuests()

    override suspend fun getAllCompletedQuests(): List<CompletedQuest> =
        loreEntryDao.getAllCompletedQuestsList().map { it.toDomain() }

    override suspend fun getAllLoreEntriesList(): List<LoreEntry> =
        loreEntryDao.getAllLoreEntriesList().map { it.toDomain() }
}

