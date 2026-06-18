package com.pauls.lorebound.ui.quest

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pauls.lorebound.domain.model.CompletedQuest
import com.pauls.lorebound.domain.model.LoreEntry
import com.pauls.lorebound.domain.model.Quest
import com.pauls.lorebound.domain.model.Title
import com.pauls.lorebound.domain.model.VerificationPayload
import com.pauls.lorebound.domain.model.VerificationRequirement
import com.pauls.lorebound.domain.model.VerificationType
import com.pauls.lorebound.domain.repository.CharacterRepository
import com.pauls.lorebound.domain.repository.LoreRepository
import com.pauls.lorebound.domain.repository.QuestRepository
import com.pauls.lorebound.domain.service.QuestVerificationService
import com.pauls.lorebound.domain.service.RankService
import com.pauls.lorebound.domain.service.TimeProvider
import com.pauls.lorebound.domain.service.TitleEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestDetailState(
    val quest: Quest? = null,
    val isCompleted: Boolean = false,
    val activeQuestId: Long = 0L,
    val isLoading: Boolean = true,
    val showCompletionDialog: Boolean = false,
    // Verification inputs
    val loreNotes: String = "",
    val photoUri: Uri? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val link: String = "",
    val locationCaptured: Boolean = false,
    val locationLoading: Boolean = false,
    // Validation
    val validationError: String? = null,
    // Submission
    val isSubmitting: Boolean = false,
    val completionSuccess: Boolean = false,
    val xpAwarded: Int = 0,
    val rankAfter: Int = 0,
    val unlockedTitle: Title? = null
)

@HiltViewModel
class QuestDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val questRepository: QuestRepository,
    private val characterRepository: CharacterRepository,
    private val loreRepository: LoreRepository,
    private val rankService: RankService,
    private val titleEngine: TitleEngine,
    private val timeProvider: TimeProvider,
    private val verificationService: QuestVerificationService
) : ViewModel() {

    private val activeQuestId: Long = savedStateHandle["questId"] ?: 0L

    private val _state = MutableStateFlow(QuestDetailState())
    val state: StateFlow<QuestDetailState> = _state.asStateFlow()

    init {
        loadQuest()
    }

    private fun loadQuest() {
        viewModelScope.launch {
            val activeQuest = questRepository.getActiveQuestById(activeQuestId)

            if (activeQuest != null) {
                val quest = questRepository.getQuestById(activeQuest.questId)
                _state.update {
                    it.copy(
                        quest = quest,
                        isCompleted = activeQuest.isCompleted,
                        activeQuestId = activeQuest.id,
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun showCompletionDialog() {
        _state.update { it.copy(showCompletionDialog = true, validationError = null) }
    }

    fun dismissCompletionDialog() {
        _state.update {
            it.copy(
                showCompletionDialog = false,
                loreNotes = "",
                photoUri = null,
                link = "",
                validationError = null
            )
        }
    }

    fun updateLoreNotes(notes: String) {
        _state.update { it.copy(loreNotes = notes, validationError = null) }
    }

    fun updatePhotoUri(uri: Uri?) {
        _state.update { it.copy(photoUri = uri, validationError = null) }
    }

    fun updateLink(link: String) {
        _state.update { it.copy(link = link, validationError = null) }
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        _state.update {
            it.copy(
                latitude = latitude,
                longitude = longitude,
                locationCaptured = true,
                locationLoading = false,
                validationError = null
            )
        }
    }

    fun setLocationLoading(loading: Boolean) {
        _state.update { it.copy(locationLoading = loading) }
    }

    fun dismissTitleDialog() {
        _state.update { it.copy(unlockedTitle = null) }
    }

    fun conquerQuest() {
        val quest = _state.value.quest ?: return
        val currentState = _state.value

        // Build verification payload
        val payload = VerificationPayload(
            photoUri = currentState.photoUri?.toString(),
            text = currentState.loreNotes.ifBlank { null },
            latitude = currentState.latitude,
            longitude = currentState.longitude,
            link = currentState.link.ifBlank { null }
        )

        // Validate
        val requirement = VerificationRequirement.from(quest.verificationType)
        val result = verificationService.validate(requirement, payload)

        if (!result.isSuccess) {
            val failure = result as com.pauls.lorebound.domain.model.VerificationResult.Failure
            _state.update { it.copy(validationError = failure.message) }
            return
        }

        _state.update { it.copy(isSubmitting = true, validationError = null) }

        viewModelScope.launch {
            val character = characterRepository.getCharacter().filterNotNull().first()
            val today = timeProvider.todayDate()

            // 1. Create completed quest record
            val completedQuest = CompletedQuest(
                questId = quest.id,
                verificationType = quest.verificationType,
                photoUri = currentState.photoUri?.toString(),
                latitude = currentState.latitude,
                longitude = currentState.longitude
            )
            val completedQuestId = loreRepository.insertCompletedQuest(completedQuest)

            // 2. Update character XP, traits, and streak
            var updatedCharacter = character.copy(
                totalXp = character.totalXp + quest.xpReward,
                lastActiveDate = today
            )
            val affectedTraits = listOfNotNull(quest.primaryAttribute, quest.secondaryAttribute)
            for (trait in affectedTraits) {
                updatedCharacter = updatedCharacter.withTraitIncrease(trait, 1)
            }
            if (updatedCharacter.currentStreak > updatedCharacter.longestStreak) {
                updatedCharacter = updatedCharacter.copy(longestStreak = updatedCharacter.currentStreak)
            }
            characterRepository.updateCharacter(updatedCharacter)

            // 3. Create lore entry
            val newRank = rankService.rankForXp(updatedCharacter.totalXp)
            val loreEntry = LoreEntry(
                questId = quest.id,
                completedQuestId = completedQuestId,
                date = today,
                questTitle = quest.title,
                xpEarned = quest.xpReward,
                traitsImproved = affectedTraits,
                userNotes = currentState.loreNotes.ifBlank { null },
                photoUri = currentState.photoUri?.toString(),
                latitude = currentState.latitude,
                longitude = currentState.longitude,
                rankAtCompletion = newRank
            )
            loreRepository.insertLoreEntry(loreEntry)

            // 4. Mark active quest completed
            questRepository.markActiveQuestCompleted(_state.value.activeQuestId, today)

            // 5. Check for title unlocks
            val newTitles = titleEngine.checkAndUnlockTitles()

            _state.update {
                it.copy(
                    isSubmitting = false,
                    showCompletionDialog = false,
                    isCompleted = true,
                    completionSuccess = true,
                    xpAwarded = quest.xpReward,
                    rankAfter = newRank,
                    unlockedTitle = newTitles.firstOrNull()
                )
            }
        }
    }
}
