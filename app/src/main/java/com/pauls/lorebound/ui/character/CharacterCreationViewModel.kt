package com.pauls.lorebound.ui.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pauls.lorebound.domain.model.Character
import com.pauls.lorebound.domain.model.Trait
import com.pauls.lorebound.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CharacterCreationState(
    val step: Int = 1,
    val name: String = "",
    val strongTraits: Set<Trait> = emptySet(),
    val weakTraits: Set<Trait> = emptySet(),
    val isCreating: Boolean = false,
    val isComplete: Boolean = false,
    val nameError: String? = null
) {
    val canProceedToStep2: Boolean
        get() = name.isNotBlank() && name.length >= 2

    val canProceedToStep3: Boolean
        get() = strongTraits.size == 2

    val canCreate: Boolean
        get() = canProceedToStep2 && strongTraits.size == 2 && weakTraits.size == 2

    fun traitValue(trait: Trait): Int = when {
        trait in strongTraits -> 8
        trait in weakTraits -> 3
        else -> 5
    }

    fun traitLabel(trait: Trait): String = when {
        trait in strongTraits -> "Greatest"
        trait in weakTraits -> "Untapped"
        else -> "Neutral"
    }
}

@HiltViewModel
class CharacterCreationViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CharacterCreationState())
    val state: StateFlow<CharacterCreationState> = _state.asStateFlow()

    fun updateName(name: String) {
        _state.update {
            it.copy(
                name = name,
                nameError = if (name.isBlank()) "Your adventurer needs a name" else null
            )
        }
    }

    fun proceedToStep2() {
        if (_state.value.canProceedToStep2) {
            _state.update { it.copy(step = 2, nameError = null) }
        } else {
            _state.update { it.copy(nameError = "Your adventurer needs a name") }
        }
    }

    fun proceedToStep3() {
        if (_state.value.canProceedToStep3) {
            _state.update { it.copy(step = 3) }
        }
    }

    fun goBackToStep1() {
        _state.update { it.copy(step = 1) }
    }

    fun goBackToStep2() {
        _state.update { it.copy(step = 2, weakTraits = emptySet()) }
    }

    fun toggleStrength(trait: Trait) {
        _state.update { current ->
            if (trait in current.strongTraits) {
                current.copy(strongTraits = current.strongTraits - trait)
            } else if (current.strongTraits.size < 2) {
                current.copy(strongTraits = current.strongTraits + trait)
            } else {
                current
            }
        }
    }

    fun toggleWeakness(trait: Trait) {
        _state.update { current ->
            if (trait in current.weakTraits) {
                current.copy(weakTraits = current.weakTraits - trait)
            } else if (current.weakTraits.size < 2) {
                current.copy(weakTraits = current.weakTraits + trait)
            } else {
                current
            }
        }
    }

    fun createCharacter() {
        val current = _state.value
        if (!current.canCreate) return

        _state.update { it.copy(isCreating = true) }

        viewModelScope.launch {
            val character = Character(
                name = current.name.trim(),
                strength = current.traitValue(Trait.STRENGTH),
                intelligence = current.traitValue(Trait.INTELLIGENCE),
                charisma = current.traitValue(Trait.CHARISMA),
                creativity = current.traitValue(Trait.CREATIVITY),
                exploration = current.traitValue(Trait.EXPLORATION),
                courage = current.traitValue(Trait.COURAGE)
            )
            characterRepository.createCharacter(character)
            _state.update { it.copy(isCreating = false, isComplete = true) }
        }
    }
}
