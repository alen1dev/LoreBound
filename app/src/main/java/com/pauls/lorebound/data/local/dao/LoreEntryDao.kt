package com.pauls.lorebound.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pauls.lorebound.data.local.entity.CompletedQuestEntity
import com.pauls.lorebound.data.local.entity.LoreEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoreEntryDao {

    @Query("SELECT * FROM lore_entries ORDER BY date DESC, id DESC")
    fun getAllLoreEntries(): Flow<List<LoreEntryEntity>>

    @Query("SELECT * FROM lore_entries WHERE date = :date ORDER BY id DESC")
    fun getLoreEntriesByDate(date: String): Flow<List<LoreEntryEntity>>

    @Query("SELECT * FROM lore_entries WHERE id = :id")
    suspend fun getLoreEntryById(id: Long): LoreEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(loreEntry: LoreEntryEntity): Long

    @Update
    suspend fun update(loreEntry: LoreEntryEntity)

    @Query("DELETE FROM lore_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM lore_entries")
    suspend fun getCount(): Int

    @Query("SELECT * FROM lore_entries WHERE questTitle LIKE '%' || :query || '%' OR userNotes LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' OR locationName LIKE '%' || :query || '%' ORDER BY date DESC, id DESC")
    fun search(query: String): Flow<List<LoreEntryEntity>>

    @Query("SELECT * FROM lore_entries WHERE isPersonal = 1 ORDER BY date DESC, id DESC")
    fun getPersonalEntries(): Flow<List<LoreEntryEntity>>

    @Query("SELECT * FROM lore_entries WHERE isPersonal = 0 ORDER BY date DESC, id DESC")
    fun getQuestEntries(): Flow<List<LoreEntryEntity>>

    @Query("SELECT * FROM lore_entries WHERE isFavorite = 1 ORDER BY date DESC, id DESC")
    fun getFavoriteEntries(): Flow<List<LoreEntryEntity>>

    // Completed quests
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedQuest(completedQuest: CompletedQuestEntity): Long

    @Query("SELECT COUNT(*) FROM completed_quests")
    suspend fun getCompletedQuestCount(): Int

    @Query("""
        SELECT COUNT(*) FROM completed_quests cq 
        INNER JOIN quests q ON cq.questId = q.id 
        WHERE q.primaryAttribute = :attribute OR q.secondaryAttribute = :attribute
    """)
    suspend fun getCompletedQuestCountByAttribute(attribute: String): Int

    @Query("SELECT COUNT(DISTINCT latitude || ',' || longitude) FROM completed_quests WHERE latitude IS NOT NULL AND longitude IS NOT NULL")
    suspend fun getDistinctLocationCount(): Int

    @Query("DELETE FROM lore_entries")
    suspend fun deleteAll()

    @Query("DELETE FROM completed_quests")
    suspend fun deleteAllCompletedQuests()

    @Query("SELECT * FROM completed_quests ORDER BY completedAt DESC")
    suspend fun getAllCompletedQuestsList(): List<CompletedQuestEntity>

    @Query("SELECT * FROM lore_entries ORDER BY date DESC")
    suspend fun getAllLoreEntriesList(): List<LoreEntryEntity>
}

