package com.pauls.lorebound.domain.repository

import com.pauls.lorebound.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharacter(): Flow<Character?>
    suspend fun createCharacter(character: Character)
    suspend fun updateCharacter(character: Character)
    suspend fun deleteCharacter()
    suspend fun characterExists(): Boolean
}

