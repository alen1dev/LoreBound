package com.pauls.lorebound.domain.repository

import com.pauls.lorebound.domain.model.Title
import kotlinx.coroutines.flow.Flow

interface TitleRepository {
    fun getAllTitles(): Flow<List<Title>>
    fun getUnlockedTitles(): Flow<List<Title>>
    fun getLockedTitles(): Flow<List<Title>>
    suspend fun getTitleById(id: String): Title?
    suspend fun unlockTitle(id: String, unlockedAt: Long = System.currentTimeMillis())
    suspend fun insertTitles(titles: List<Title>)
    suspend fun getTitleCount(): Int
    suspend fun deleteAllTitles()
}

