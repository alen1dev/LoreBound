package com.pauls.lorebound.domain.service

import com.pauls.lorebound.domain.model.Character
import com.pauls.lorebound.domain.model.Quest
import com.pauls.lorebound.domain.model.QuestType

interface QuestGenerator {
    suspend fun generateDailyQuests(character: Character): List<Quest>
    suspend fun generateQuestsForType(character: Character, questType: QuestType, count: Int): List<Quest>
}
