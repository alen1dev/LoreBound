package com.pauls.lorebound.domain.repository

import com.pauls.lorebound.domain.model.Feat
import kotlinx.coroutines.flow.Flow

interface FeatRepository {
    fun getAllFeats(): Flow<List<Feat>>
    fun getUnlockedFeats(): Flow<List<Feat>>
    fun getLockedFeats(): Flow<List<Feat>>
    suspend fun getFeatById(id: String): Feat?
    suspend fun unlockFeat(id: String, unlockedAt: Long = System.currentTimeMillis())
    suspend fun updateFeatProgress(id: String, currentValue: Int)
    suspend fun insertFeats(feats: List<Feat>)
    suspend fun getFeatCount(): Int
    suspend fun deleteAllFeats()
}

