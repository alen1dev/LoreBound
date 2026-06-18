package com.pauls.lorebound.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pauls.lorebound.data.local.entity.ActiveQuestEntity
import com.pauls.lorebound.data.local.entity.DailyQuestEntity
import com.pauls.lorebound.data.local.entity.QuestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {

    @Query("SELECT * FROM quests")
    fun getAllQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE id = :id")
    suspend fun getQuestById(id: Long): QuestEntity?

    @Query("SELECT * FROM quests WHERE slug = :slug")
    suspend fun getQuestBySlug(slug: String): QuestEntity?

    @Query("SELECT * FROM quests WHERE primaryAttribute = :attribute OR secondaryAttribute = :attribute")
    suspend fun getQuestsByAttribute(attribute: String): List<QuestEntity>

    @Query("SELECT * FROM quests WHERE difficulty = :difficulty")
    suspend fun getQuestsByDifficulty(difficulty: Int): List<QuestEntity>

    @Query("SELECT * FROM quests WHERE primaryAttribute = :attribute AND difficulty = :difficulty")
    suspend fun getQuestsByAttributeAndDifficulty(attribute: String, difficulty: Int): List<QuestEntity>

    @Query("SELECT * FROM quests WHERE questType = :questType")
    suspend fun getQuestsByType(questType: String): List<QuestEntity>

    @Query("SELECT * FROM quests WHERE rarity = :rarity")
    suspend fun getQuestsByRarity(rarity: String): List<QuestEntity>

    @Query("SELECT * FROM quests WHERE tags LIKE '%' || :tag || '%'")
    suspend fun getQuestsByTag(tag: String): List<QuestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quest: QuestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(quests: List<QuestEntity>)

    @Query("SELECT COUNT(*) FROM quests")
    suspend fun getCount(): Int

    // Daily quests (legacy - kept for backward compat)
    @Query("SELECT * FROM daily_quests WHERE date = :date")
    fun getDailyQuests(date: String): Flow<List<DailyQuestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyQuests(dailyQuests: List<DailyQuestEntity>)

    @Query("UPDATE daily_quests SET isCompleted = 1 WHERE id = :id")
    suspend fun markDailyQuestCompleted(id: Long)

    @Query("SELECT COUNT(*) FROM daily_quests WHERE date = :date")
    suspend fun getDailyQuestCountForDate(date: String): Int

    @Query("DELETE FROM daily_quests WHERE date = :date")
    suspend fun clearDailyQuestsForDate(date: String)

    // Active quests (new layered system) — includes completed so they show as struck-through
    @Query("SELECT * FROM active_quests WHERE questType = :questType AND expiresDate >= :today")
    fun getActiveQuestsByType(questType: String, today: String): Flow<List<ActiveQuestEntity>>

    @Query("SELECT * FROM active_quests WHERE isCompleted = 0 AND expiresDate >= :today")
    fun getAllActiveQuests(today: String): Flow<List<ActiveQuestEntity>>

    @Query("SELECT * FROM active_quests WHERE id = :id")
    suspend fun getActiveQuestById(id: Long): ActiveQuestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActiveQuests(quests: List<ActiveQuestEntity>)

    @Query("UPDATE active_quests SET isCompleted = 1, completedDate = :completedDate WHERE id = :id")
    suspend fun markActiveQuestCompleted(id: Long, completedDate: String)

    @Query("SELECT COUNT(*) FROM active_quests WHERE questType = :questType AND expiresDate >= :today")
    suspend fun getActiveQuestCountByType(questType: String, today: String): Int

    @Query("DELETE FROM active_quests WHERE expiresDate < :today AND isCompleted = 0")
    suspend fun clearExpiredQuests(today: String)

    @Query("DELETE FROM active_quests WHERE isCompleted = 0")
    suspend fun clearAllActiveQuests()

    @Query("DELETE FROM active_quests")
    suspend fun deleteAllActiveQuests()

    @Query("DELETE FROM active_quests WHERE questType = :questType")
    suspend fun deleteActiveQuestsByType(questType: String)
}
