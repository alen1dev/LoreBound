package com.pauls.lorebound.domain.service

import com.pauls.lorebound.domain.model.Character

interface AdventureCoach {
    suspend fun getMotivation(character: Character): String
    suspend fun getSuggestion(character: Character): String
}

