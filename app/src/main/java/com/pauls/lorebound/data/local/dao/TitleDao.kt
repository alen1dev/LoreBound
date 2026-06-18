package com.pauls.lorebound.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pauls.lorebound.data.local.entity.TitleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TitleDao {

    @Query("SELECT * FROM titles ORDER BY requiredQuestCount ASC")
    fun getAllTitles(): Flow<List<TitleEntity>>

    @Query("SELECT * FROM titles WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedTitles(): Flow<List<TitleEntity>>

    @Query("SELECT * FROM titles WHERE isUnlocked = 0 ORDER BY requiredQuestCount ASC")
    fun getLockedTitles(): Flow<List<TitleEntity>>

    @Query("SELECT * FROM titles WHERE id = :id")
    suspend fun getTitleById(id: String): TitleEntity?

    @Query("UPDATE titles SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE id = :id")
    suspend fun unlockTitle(id: String, unlockedAt: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(titles: List<TitleEntity>)

    @Query("SELECT COUNT(*) FROM titles")
    suspend fun getCount(): Int

    @Query("DELETE FROM titles")
    suspend fun deleteAll()
}

