package com.pauls.lorebound.domain.service

import com.pauls.lorebound.domain.model.Character
import com.pauls.lorebound.domain.model.Quest
import com.pauls.lorebound.domain.model.QuestType
import com.pauls.lorebound.domain.model.Trait
import com.pauls.lorebound.domain.repository.QuestRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalQuestGenerator @Inject constructor(
    private val questRepository: QuestRepository
) : QuestGenerator {

    override suspend fun generateDailyQuests(character: Character): List<Quest> {
        return generateQuestsForType(character, QuestType.DAILY, 1)
    }

    override suspend fun generateQuestsForType(
        character: Character,
        questType: QuestType,
        count: Int
    ): List<Quest> {
        val weakTraits = character.weakTraits()

        val candidates = questRepository.getQuestsByType(questType)
            .let { quests ->
                // Only filter to difficulty 1 for daily quests
                if (questType == QuestType.DAILY) quests.filter { it.difficulty == 1 }
                else quests
            }

        val fromWeakTraits = candidates
            .filter { quest ->
                quest.primaryAttribute in weakTraits ||
                        (quest.secondaryAttribute != null && quest.secondaryAttribute in weakTraits)
            }
            .shuffled()

        val allShuffled = candidates.shuffled()

        val pool = (fromWeakTraits + allShuffled).distinctBy { it.id }

        return pool.take(count)
    }
}
