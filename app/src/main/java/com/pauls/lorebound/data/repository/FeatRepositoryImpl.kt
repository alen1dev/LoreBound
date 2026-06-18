package com.pauls.lorebound.data.repository

import com.pauls.lorebound.data.local.dao.FeatDao
import com.pauls.lorebound.data.local.mapper.toDomain
import com.pauls.lorebound.data.local.mapper.toEntity
import com.pauls.lorebound.domain.model.Feat
import com.pauls.lorebound.domain.repository.FeatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatRepositoryImpl @Inject constructor(
    private val featDao: FeatDao
) : FeatRepository {

    override fun getAllFeats(): Flow<List<Feat>> =
        featDao.getAllFeats().map { entities -> entities.map { it.toDomain() } }

    override fun getUnlockedFeats(): Flow<List<Feat>> =
        featDao.getUnlockedFeats().map { entities -> entities.map { it.toDomain() } }

    override fun getLockedFeats(): Flow<List<Feat>> =
        featDao.getLockedFeats().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getFeatById(id: String): Feat? =
        featDao.getFeatById(id)?.toDomain()

    override suspend fun unlockFeat(id: String, unlockedAt: Long) =
        featDao.unlockFeat(id, unlockedAt)

    override suspend fun updateFeatProgress(id: String, currentValue: Int) =
        featDao.updateProgress(id, currentValue)

    override suspend fun insertFeats(feats: List<Feat>) =
        featDao.insertAll(feats.map { it.toEntity() })

    override suspend fun getFeatCount(): Int =
        featDao.getCount()

    override suspend fun deleteAllFeats() =
        featDao.deleteAll()
}

