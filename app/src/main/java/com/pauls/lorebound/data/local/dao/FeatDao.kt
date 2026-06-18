package com.pauls.lorebound.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pauls.lorebound.data.local.entity.FeatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeatDao {

    @Query("SELECT * FROM feats ORDER BY targetValue ASC")
    fun getAllFeats(): Flow<List<FeatEntity>>

    @Query("SELECT * FROM feats WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedFeats(): Flow<List<FeatEntity>>

    @Query("SELECT * FROM feats WHERE isUnlocked = 0 ORDER BY targetValue ASC")
    fun getLockedFeats(): Flow<List<FeatEntity>>

    @Query("SELECT * FROM feats WHERE id = :id")
    suspend fun getFeatById(id: String): FeatEntity?

    @Query("UPDATE feats SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE id = :id")
    suspend fun unlockFeat(id: String, unlockedAt: Long)

    @Query("UPDATE feats SET currentValue = :currentValue WHERE id = :id")
    suspend fun updateProgress(id: String, currentValue: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(feats: List<FeatEntity>)

    @Query("SELECT COUNT(*) FROM feats")
    suspend fun getCount(): Int

    @Query("DELETE FROM feats")
    suspend fun deleteAll()
}

