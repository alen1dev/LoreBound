package com.pauls.lorebound.domain.repository

import com.pauls.lorebound.domain.model.ActiveQuest
import com.pauls.lorebound.domain.model.DailyQuest
import com.pauls.lorebound.domain.model.Quest
import com.pauls.lorebound.domain.model.QuestType
import com.pauls.lorebound.domain.model.Rarity
import com.pauls.lorebound.domain.model.Trait
import kotlinx.coroutines.flow.Flow

interface QuestRepository {
    fun getAllQuests(): Flow<List<Quest>>
    suspend fun getQuestById(id: Long): Quest?
    suspend fun getQuestBySlug(slug: String): Quest?
    suspend fun getQuestsByAttribute(attribute: Trait): List<Quest>
    suspend fun getQuestsByDifficulty(difficulty: Int): List<Quest>
    suspend fun getQuestsByAttributeAndDifficulty(attribute: Trait, difficulty: Int): List<Quest>
    suspend fun getQuestsByType(questType: QuestType): List<Quest>
    suspend fun getQuestsByRarity(rarity: Rarity): List<Quest>
    suspend fun getQuestsByTag(tag: String): List<Quest>
    suspend fun insertQuest(quest: Quest): Long
    suspend fun insertQuests(quests: List<Quest>)
    suspend fun getQuestCount(): Int

    // Daily quests (legacy)
    fun getDailyQuests(date: String): Flow<List<DailyQuest>>
    suspend fun insertDailyQuests(dailyQuests: List<DailyQuest>)
    suspend fun markDailyQuestCompleted(id: Long)
    suspend fun dailyQuestsExistForDate(date: String): Boolean
    suspend fun clearDailyQuestsForDate(date: String)

    // Active quests (layered system)
    fun getActiveQuestsByType(questType: QuestType, today: String): Flow<List<ActiveQuest>>
    fun getAllActiveQuests(today: String): Flow<List<ActiveQuest>>
    suspend fun getActiveQuestById(id: Long): ActiveQuest?
    suspend fun insertActiveQuests(quests: List<ActiveQuest>)
    suspend fun markActiveQuestCompleted(id: Long, completedDate: String)
    suspend fun getActiveQuestCountByType(questType: QuestType, today: String): Int
    suspend fun clearExpiredQuests(today: String)
    suspend fun clearAllActiveQuests()
    suspend fun deleteAllActiveQuests()
    suspend fun deleteActiveQuestsByType(questType: QuestType)
}
