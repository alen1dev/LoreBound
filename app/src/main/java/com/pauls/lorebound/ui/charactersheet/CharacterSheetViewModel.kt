package com.pauls.lorebound.ui.charactersheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pauls.lorebound.domain.model.Character
import com.pauls.lorebound.domain.model.Title
import com.pauls.lorebound.domain.repository.CharacterRepository
import com.pauls.lorebound.domain.repository.LoreRepository
import com.pauls.lorebound.domain.repository.TitleRepository
import com.pauls.lorebound.domain.service.RankService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CharacterSheetState(
    val character: Character? = null,
    val rank: Int = 1,
    val rankProgress: Float = 0f,
    val xpToNextRank: Long = 0L,
    val totalQuestsCompleted: Int = 0,
    val unlockedTitles: List<Title> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CharacterSheetViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val loreRepository: LoreRepository,
    private val titleRepository: TitleRepository,
    private val rankService: RankService
) : ViewModel() {

    private val _state = MutableStateFlow(CharacterSheetState())
    val state: StateFlow<CharacterSheetState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                characterRepository.getCharacter().filterNotNull(),
                titleRepository.getUnlockedTitles()
            ) { character, titles ->
                val totalCompleted = loreRepository.getCompletedQuestCount()
                val rank = rankService.rankForXp(character.totalXp)
                val progress = rankService.progressToNextRank(character.totalXp)
                val xpToNext = rankService.xpToNextRank(character.totalXp)

                CharacterSheetState(
                    character = character,
                    rank = rank,
                    rankProgress = progress,
                    xpToNextRank = xpToNext,
                    totalQuestsCompleted = totalCompleted,
                    unlockedTitles = titles,
                    isLoading = false
                )
            }.collect { newState ->
                _state.update { newState }
            }
        }
    }
}

