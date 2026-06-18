package com.pauls.lorebound.data.repository

import com.pauls.lorebound.data.local.dao.QuestDao
import com.pauls.lorebound.data.local.mapper.toDomain
import com.pauls.lorebound.data.local.mapper.toEntity
import com.pauls.lorebound.domain.model.ActiveQuest
import com.pauls.lorebound.domain.model.DailyQuest
import com.pauls.lorebound.domain.model.Quest
import com.pauls.lorebound.domain.model.QuestType
import com.pauls.lorebound.domain.model.Rarity
import com.pauls.lorebound.domain.model.Trait
import com.pauls.lorebound.domain.repository.QuestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestRepositoryImpl @Inject constructor(
    private val questDao: QuestDao
) : QuestRepository {

    override fun getAllQuests(): Flow<List<Quest>> =
        questDao.getAllQuests().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getQuestById(id: Long): Quest? =
        questDao.getQuestById(id)?.toDomain()

    override suspend fun getQuestBySlug(slug: String): Quest? =
        questDao.getQuestBySlug(slug)?.toDomain()

    override suspend fun getQuestsByAttribute(attribute: Trait): List<Quest> =
        questDao.getQuestsByAttribute(attribute.name).map { it.toDomain() }

    override suspend fun getQuestsByDifficulty(difficulty: Int): List<Quest> =
        questDao.getQuestsByDifficulty(difficulty).map { it.toDomain() }

    override suspend fun getQuestsByAttributeAndDifficulty(
        attribute: Trait,
        difficulty: Int
    ): List<Quest> =
        questDao.getQuestsByAttributeAndDifficulty(attribute.name, difficulty).map { it.toDomain() }

    override suspend fun getQuestsByType(questType: QuestType): List<Quest> =
        questDao.getQuestsByType(questType.name).map { it.toDomain() }

    override suspend fun getQuestsByRarity(rarity: Rarity): List<Quest> =
        questDao.getQuestsByRarity(rarity.name).map { it.toDomain() }

    override suspend fun getQuestsByTag(tag: String): List<Quest> =
        questDao.getQuestsByTag(tag).map { it.toDomain() }

    override suspend fun insertQuest(quest: Quest): Long =
        questDao.insert(quest.toEntity())

    override suspend fun insertQuests(quests: List<Quest>) =
        questDao.insertAll(quests.map { it.toEntity() })

    override suspend fun getQuestCount(): Int =
        questDao.getCount()

    override fun getDailyQuests(date: String): Flow<List<DailyQuest>> =
        questDao.getDailyQuests(date).map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertDailyQuests(dailyQuests: List<DailyQuest>) =
        questDao.insertDailyQuests(dailyQuests.map { it.toEntity() })

    override suspend fun markDailyQuestCompleted(id: Long) =
        questDao.markDailyQuestCompleted(id)

    override suspend fun dailyQuestsExistForDate(date: String): Boolean =
        questDao.getDailyQuestCountForDate(date) > 0

    override suspend fun clearDailyQuestsForDate(date: String) =
        questDao.clearDailyQuestsForDate(date)

    // Active quests (layered system)
    override fun getActiveQuestsByType(questType: QuestType, today: String): Flow<List<ActiveQuest>> =
        questDao.getActiveQuestsByType(questType.name, today).map { entities -> entities.map { it.toDomain() } }

    override fun getAllActiveQuests(today: String): Flow<List<ActiveQuest>> =
        questDao.getAllActiveQuests(today).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getActiveQuestById(id: Long): ActiveQuest? =
        questDao.getActiveQuestById(id)?.toDomain()

    override suspend fun insertActiveQuests(quests: List<ActiveQuest>) =
        questDao.insertActiveQuests(quests.map { it.toEntity() })

    override suspend fun markActiveQuestCompleted(id: Long, completedDate: String) =
        questDao.markActiveQuestCompleted(id, completedDate)

    override suspend fun getActiveQuestCountByType(questType: QuestType, today: String): Int =
        questDao.getActiveQuestCountByType(questType.name, today)

    override suspend fun clearExpiredQuests(today: String) =
        questDao.clearExpiredQuests(today)

    override suspend fun clearAllActiveQuests() =
        questDao.clearAllActiveQuests()

    override suspend fun deleteAllActiveQuests() =
        questDao.deleteAllActiveQuests()

    override suspend fun deleteActiveQuestsByType(questType: QuestType) =
        questDao.deleteActiveQuestsByType(questType.name)
}
