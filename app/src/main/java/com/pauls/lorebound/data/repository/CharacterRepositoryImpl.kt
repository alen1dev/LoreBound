package com.pauls.lorebound.data.repository

import com.pauls.lorebound.data.local.dao.CharacterDao
import com.pauls.lorebound.data.local.mapper.toDomain
import com.pauls.lorebound.data.local.mapper.toEntity
import com.pauls.lorebound.domain.model.Character
import com.pauls.lorebound.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val characterDao: CharacterDao
) : CharacterRepository {

    override fun getCharacter(): Flow<Character?> =
        characterDao.getCharacter().map { it?.toDomain() }

    override suspend fun createCharacter(character: Character) {
        characterDao.insert(character.toEntity())
    }

    override suspend fun updateCharacter(character: Character) {
        characterDao.update(character.toEntity())
    }

    override suspend fun deleteCharacter() {
        characterDao.deleteAll()
    }

    override suspend fun characterExists(): Boolean =
        characterDao.getCount() > 0
}

