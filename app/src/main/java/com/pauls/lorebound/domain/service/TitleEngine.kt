package com.pauls.lorebound.domain.service

import com.pauls.lorebound.domain.model.Title
import com.pauls.lorebound.domain.repository.CharacterRepository
import com.pauls.lorebound.domain.repository.LoreRepository
import com.pauls.lorebound.domain.repository.TitleRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TitleEngine @Inject constructor(
    private val titleRepository: TitleRepository,
    private val loreRepository: LoreRepository,
    private val characterRepository: CharacterRepository
) {
    suspend fun checkAndUnlockTitles(): List<Title> {
        val totalCompleted = loreRepository.getCompletedQuestCount()
        val allTitles = titleRepository.getAllTitles().first()
        val newlyUnlocked = mutableListOf<Title>()

        for (title in allTitles) {
            if (title.isUnlocked) continue

            val count = if (title.requiredCategory != null) {
                loreRepository.getCompletedQuestCountByCategory(title.requiredCategory.name)
            } else {
                totalCompleted
            }

            if (count >= title.requiredQuestCount) {
                titleRepository.unlockTitle(title.id)
                newlyUnlocked.add(title.copy(isUnlocked = true))
            }
        }

        if (newlyUnlocked.isNotEmpty()) {
            val latest = newlyUnlocked.last()
            val character = characterRepository.getCharacter().filterNotNull().first()
            characterRepository.updateCharacter(character.copy(currentTitle = latest.name))
        }

        return newlyUnlocked
    }
}

