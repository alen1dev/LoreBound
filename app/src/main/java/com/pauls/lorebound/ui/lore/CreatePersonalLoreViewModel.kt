package com.pauls.lorebound.ui.lore

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pauls.lorebound.domain.model.LoreEntry
import com.pauls.lorebound.domain.repository.LoreRepository
import com.pauls.lorebound.domain.service.TimeProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateLoreState(
    val title: String = "",
    val story: String = "",
    val selectedTags: List<String> = emptyList(),
    val photoUri: Uri? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String = "",
    val isFavorite: Boolean = false,
    val locationCaptured: Boolean = false,
    val locationLoading: Boolean = false,
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val validationError: String? = null
)

@HiltViewModel
class CreatePersonalLoreViewModel @Inject constructor(
    private val loreRepository: LoreRepository,
    private val timeProvider: TimeProvider
) : ViewModel() {

    private val _state = MutableStateFlow(CreateLoreState())
    val state: StateFlow<CreateLoreState> = _state.asStateFlow()

    companion object {
        val SUGGESTED_TAGS = listOf(
            "Travel", "Food", "Culture", "People", "Adventure",
            "Learning", "Creative", "Nature", "Achievement", "Funny", "Unexpected"
        )
    }

    fun updateTitle(title: String) {
        _state.update { it.copy(title = title, validationError = null) }
    }

    fun updateStory(story: String) {
        _state.update { it.copy(story = story) }
    }

    fun toggleTag(tag: String) {
        _state.update { current ->
            val tags = if (tag in current.selectedTags) {
                current.selectedTags - tag
            } else {
                current.selectedTags + tag
            }
            current.copy(selectedTags = tags)
        }
    }

    fun updatePhotoUri(uri: Uri?) {
        _state.update { it.copy(photoUri = uri) }
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        _state.update {
            it.copy(
                latitude = latitude,
                longitude = longitude,
                locationCaptured = true,
                locationLoading = false
            )
        }
    }

    fun setLocationLoading(loading: Boolean) {
        _state.update { it.copy(locationLoading = loading) }
    }

    fun updateLocationName(name: String) {
        _state.update { it.copy(locationName = name) }
    }

    fun toggleFavorite() {
        _state.update { it.copy(isFavorite = !it.isFavorite) }
    }

    fun save() {
        val current = _state.value

        if (current.title.isBlank()) {
            _state.update { it.copy(validationError = "Title is required") }
            return
        }

        _state.update { it.copy(isSaving = true, validationError = null) }

        viewModelScope.launch {
            val storyWeight = calculateStoryWeight(current)
            val today = timeProvider.todayDate()

            val entry = LoreEntry(
                questId = 0L,
                completedQuestId = 0L,
                date = today,
                questTitle = current.title.trim(),
                xpEarned = 0,
                traitsImproved = emptyList(),
                userNotes = current.story.ifBlank { null },
                photoUri = current.photoUri?.toString(),
                latitude = current.latitude,
                longitude = current.longitude,
                locationName = current.locationName.ifBlank { null },
                rankAtCompletion = 0,
                isPersonal = true,
                tags = current.selectedTags,
                isFavorite = current.isFavorite,
                storyWeight = storyWeight
            )

            loreRepository.insertLoreEntry(entry)
            _state.update { it.copy(isSaving = false, savedSuccessfully = true) }
        }
    }

    private fun calculateStoryWeight(state: CreateLoreState): Int {
        var weight = 0
        if (state.photoUri != null) weight += 2
        if (state.locationCaptured) weight += 2
        if (state.story.length > 150) weight += 2
        if (state.isFavorite) weight += 3
        return weight.coerceIn(0, 10)
    }
}
