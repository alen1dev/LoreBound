package com.pauls.lorebound.data.repository

import com.pauls.lorebound.data.local.dao.TitleDao
import com.pauls.lorebound.data.local.mapper.toDomain
import com.pauls.lorebound.data.local.mapper.toEntity
import com.pauls.lorebound.domain.model.Title
import com.pauls.lorebound.domain.repository.TitleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TitleRepositoryImpl @Inject constructor(
    private val titleDao: TitleDao
) : TitleRepository {

    override fun getAllTitles(): Flow<List<Title>> =
        titleDao.getAllTitles().map { entities -> entities.map { it.toDomain() } }

    override fun getUnlockedTitles(): Flow<List<Title>> =
        titleDao.getUnlockedTitles().map { entities -> entities.map { it.toDomain() } }

    override fun getLockedTitles(): Flow<List<Title>> =
        titleDao.getLockedTitles().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getTitleById(id: String): Title? =
        titleDao.getTitleById(id)?.toDomain()

    override suspend fun unlockTitle(id: String, unlockedAt: Long) =
        titleDao.unlockTitle(id, unlockedAt)

    override suspend fun insertTitles(titles: List<Title>) =
        titleDao.insertAll(titles.map { it.toEntity() })

    override suspend fun getTitleCount(): Int =
        titleDao.getCount()

    override suspend fun deleteAllTitles() =
        titleDao.deleteAll()
}

