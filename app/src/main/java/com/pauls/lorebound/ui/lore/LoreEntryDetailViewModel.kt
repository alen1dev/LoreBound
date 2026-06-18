package com.pauls.lorebound.ui.lore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pauls.lorebound.domain.model.LoreEntry
import com.pauls.lorebound.domain.repository.LoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoreEntryDetailState(
    val entry: LoreEntry? = null,
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val editedNotes: String = ""
)

@HiltViewModel
class LoreEntryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loreRepository: LoreRepository
) : ViewModel() {

    private val entryId: Long = savedStateHandle["entryId"] ?: 0L

    private val _state = MutableStateFlow(LoreEntryDetailState())
    val state: StateFlow<LoreEntryDetailState> = _state.asStateFlow()

    init {
        loadEntry()
    }

    private fun loadEntry() {
        viewModelScope.launch {
            val entry = loreRepository.getLoreEntryById(entryId)
            _state.update {
                it.copy(
                    entry = entry,
                    isLoading = false,
                    editedNotes = entry?.userNotes ?: ""
                )
            }
        }
    }

    fun startEditing() {
        _state.update { it.copy(isEditing = true) }
    }

    fun cancelEditing() {
        _state.update {
            it.copy(
                isEditing = false,
                editedNotes = it.entry?.userNotes ?: ""
            )
        }
    }

    fun updateNotes(notes: String) {
        _state.update { it.copy(editedNotes = notes) }
    }

    fun saveNotes() {
        val entry = _state.value.entry ?: return
        viewModelScope.launch {
            val updated = entry.copy(userNotes = _state.value.editedNotes.ifBlank { null })
            loreRepository.updateLoreEntry(updated)
            _state.update {
                it.copy(
                    entry = updated,
                    isEditing = false
                )
            }
        }
    }

    fun deleteEntry(onDeleted: () -> Unit) {
        viewModelScope.launch {
            loreRepository.deleteLoreEntry(entryId)
            onDeleted()
        }
    }

    fun toggleFavorite() {
        val entry = _state.value.entry ?: return
        viewModelScope.launch {
            val updated = entry.copy(isFavorite = !entry.isFavorite)
            loreRepository.updateLoreEntry(updated)
            _state.update { it.copy(entry = updated) }
        }
    }
}

