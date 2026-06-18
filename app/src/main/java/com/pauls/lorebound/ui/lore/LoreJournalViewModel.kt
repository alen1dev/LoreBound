package com.pauls.lorebound.ui.lore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pauls.lorebound.domain.model.LoreEntry
import com.pauls.lorebound.domain.repository.LoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LoreFilter {
    ALL, QUEST, PERSONAL, FAVORITES
}

data class LoreJournalState(
    val entries: List<LoreEntry> = emptyList(),
    val groupedEntries: Map<String, List<LoreEntry>> = emptyMap(),
    val searchQuery: String = "",
    val filter: LoreFilter = LoreFilter.ALL,
    val isLoading: Boolean = true,
    val totalEntries: Int = 0,
    val totalXp: Long = 0L
)

@HiltViewModel
class LoreJournalViewModel @Inject constructor(
    private val loreRepository: LoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoreJournalState())
    val state: StateFlow<LoreJournalState> = _state.asStateFlow()

    private var observeJob: Job? = null

    init {
        observeEntries()
    }

    private fun observeEntries() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            val flow = when (_state.value.filter) {
                LoreFilter.ALL -> loreRepository.getAllLoreEntries()
                LoreFilter.QUEST -> loreRepository.getQuestEntries()
                LoreFilter.PERSONAL -> loreRepository.getPersonalEntries()
                LoreFilter.FAVORITES -> loreRepository.getFavoriteEntries()
            }
            flow.collect { entries ->
                _state.update {
                    it.copy(
                        entries = entries,
                        groupedEntries = entries.groupBy { entry -> entry.date },
                        isLoading = false,
                        totalEntries = entries.size,
                        totalXp = entries.sumOf { entry -> entry.xpEarned.toLong() }
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            if (query.isBlank()) {
                observeEntries()
            } else {
                loreRepository.searchLoreEntries(query).collect { entries ->
                    _state.update {
                        it.copy(
                            entries = entries,
                            groupedEntries = entries.groupBy { entry -> entry.date },
                            totalEntries = entries.size,
                            totalXp = entries.sumOf { entry -> entry.xpEarned.toLong() }
                        )
                    }
                }
            }
        }
    }

    fun setFilter(filter: LoreFilter) {
        _state.update { it.copy(filter = filter, searchQuery = "") }
        observeEntries()
    }
}

