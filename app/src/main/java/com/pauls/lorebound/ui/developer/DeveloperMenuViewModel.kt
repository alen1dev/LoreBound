package com.pauls.lorebound.ui.developer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pauls.lorebound.domain.ai.AiKeyStore
import com.pauls.lorebound.domain.ai.AiProviderType
import com.pauls.lorebound.domain.ai.ChronicleGenerationResult
import com.pauls.lorebound.domain.ai.ChronicleGenerator
import com.pauls.lorebound.domain.ai.ChronicleStorage
import com.pauls.lorebound.domain.ai.FakeYearGenerator
import com.pauls.lorebound.domain.ai.GeminiProvider
import com.pauls.lorebound.domain.model.ActiveQuest
import com.pauls.lorebound.domain.model.Chronicle
import com.pauls.lorebound.domain.model.QuestType
import com.pauls.lorebound.domain.repository.CharacterRepository
import com.pauls.lorebound.domain.repository.FeatRepository
import com.pauls.lorebound.domain.repository.LoreRepository
import com.pauls.lorebound.domain.repository.QuestRepository
import com.pauls.lorebound.domain.repository.TitleRepository
import com.pauls.lorebound.domain.service.DebugTimeProvider
import com.pauls.lorebound.domain.service.QuestGenerator
import com.pauls.lorebound.domain.service.RankService
import com.pauls.lorebound.domain.service.TimeProvider
import com.pauls.lorebound.data.seed.TitleFeatAssetLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class DevMenuState(
    val timeOffsetDays: Long = 0L,
    val currentSimulatedDate: String = "",
    // AI state
    val selectedProvider: AiProviderType = AiProviderType.DISABLED,
    val geminiApiKey: String = "",
    val isApiKeyVisible: Boolean = false,
    val connectionTestResult: String? = null,
    val isTestingConnection: Boolean = false,
    // Chronicle state
    val isGeneratingChronicle: Boolean = false,
    val chronicleJson: String? = null,
    val chroniclePreview: Chronicle? = null,
    val chronicleError: String? = null,
    val isFakeYearGenerating: Boolean = false,
    val fakeYearMessage: String? = null
)

sealed interface DevMenuEvent {
    data object NavigateToCharacterCreation : DevMenuEvent
    data object RestartHome : DevMenuEvent
    data class ShowMessage(val message: String) : DevMenuEvent
}

@HiltViewModel
class DeveloperMenuViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val questRepository: QuestRepository,
    private val loreRepository: LoreRepository,
    private val titleRepository: TitleRepository,
    private val featRepository: FeatRepository,
    private val questGenerator: QuestGenerator,
    private val rankService: RankService,
    private val timeProvider: TimeProvider,
    private val titleFeatAssetLoader: TitleFeatAssetLoader,
    private val aiKeyStore: AiKeyStore,
    private val geminiProvider: GeminiProvider,
    private val chronicleGenerator: ChronicleGenerator,
    private val chronicleStorage: ChronicleStorage,
    private val fakeYearGenerator: FakeYearGenerator
) : ViewModel() {

    private val _state = MutableStateFlow(DevMenuState())
    val state: StateFlow<DevMenuState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<DevMenuEvent>()
    val events: SharedFlow<DevMenuEvent> = _events.asSharedFlow()

    private val debugTimeProvider: DebugTimeProvider?
        get() = timeProvider as? DebugTimeProvider

    init {
        refreshState()
    }

    private fun refreshState() {
        _state.update {
            it.copy(
                timeOffsetDays = debugTimeProvider?.getOffsetDays() ?: 0L,
                currentSimulatedDate = timeProvider.todayDate(),
                selectedProvider = aiKeyStore.getSelectedProvider(),
                geminiApiKey = aiKeyStore.getGeminiApiKey() ?: ""
            )
        }
    }

    // ── AI Provider Controls ─────────────────────────

    fun setAiProvider(type: AiProviderType) {
        aiKeyStore.setSelectedProvider(type)
        _state.update { it.copy(selectedProvider = type) }
    }

    fun setGeminiApiKey(key: String) {
        _state.update { it.copy(geminiApiKey = key) }
        aiKeyStore.setGeminiApiKey(key)
    }

    fun toggleApiKeyVisibility() {
        _state.update { it.copy(isApiKeyVisible = !it.isApiKeyVisible) }
    }

    fun testConnection() {
        viewModelScope.launch {
            _state.update { it.copy(isTestingConnection = true, connectionTestResult = null) }
            val result = withContext(Dispatchers.IO) {
                geminiProvider.testConnection()
            }
            _state.update {
                it.copy(
                    isTestingConnection = false,
                    connectionTestResult = if (result.success) "✓ Connected (${result.latencyMs}ms)" else "✗ ${result.message}"
                )
            }
        }
    }

    // ── Chronicle Controls ───────────────────────────

    fun generateChronicleJson() {
        viewModelScope.launch {
            _state.update { it.copy(isGeneratingChronicle = true, chronicleError = null, chronicleJson = null) }
            val character = characterRepository.getCharacter().filterNotNull().first()
            val completedQuests = loreRepository.getAllCompletedQuests()
            val loreEntries = loreRepository.getAllLoreEntriesList()
            val year = timeProvider.todayDate().take(4).toIntOrNull() ?: 2026

            val result = withContext(Dispatchers.IO) {
                chronicleGenerator.generateChronicle(character, completedQuests, loreEntries, year)
            }

            when (result) {
                is ChronicleGenerationResult.Success -> {
                    chronicleStorage.saveChronicleJson(year, result.rawJson)
                    _state.update {
                        it.copy(
                            isGeneratingChronicle = false,
                            chronicleJson = result.rawJson,
                            chroniclePreview = result.chronicle
                        )
                    }
                }
                is ChronicleGenerationResult.PartialSuccess -> {
                    chronicleStorage.saveChronicleJson(year, result.rawJson)
                    _state.update {
                        it.copy(
                            isGeneratingChronicle = false,
                            chronicleJson = result.rawJson,
                            chronicleError = result.message
                        )
                    }
                }
                is ChronicleGenerationResult.Error -> {
                    _state.update {
                        it.copy(isGeneratingChronicle = false, chronicleError = result.message)
                    }
                }
            }
        }
    }

    fun simulateEndOfYear() {
        val year = java.time.LocalDate.now().year
        debugTimeProvider?.setToDate("$year-12-28")
        refreshState()
        viewModelScope.launch {
            _events.emit(DevMenuEvent.ShowMessage("Simulated: $year-12-28"))
            _events.emit(DevMenuEvent.RestartHome)
        }
    }

    fun generateFakeYear() {
        viewModelScope.launch {
            _state.update { it.copy(isFakeYearGenerating = true, fakeYearMessage = null) }
            val result = fakeYearGenerator.generateFakeYear()
            _state.update {
                it.copy(
                    isFakeYearGenerating = false,
                    fakeYearMessage = "Generated: ${result.questsCompleted} quests, ${result.loreEntries} lore, ${result.locations} locations, ${result.photos} photos, ${result.titlesUnlocked} titles, +${result.xpGranted} XP"
                )
            }
        }
    }

    fun resetChronicleData() {
        chronicleStorage.deleteAll()
        _state.update { it.copy(chronicleJson = null, chroniclePreview = null, chronicleError = null, fakeYearMessage = null) }
        viewModelScope.launch {
            _events.emit(DevMenuEvent.ShowMessage("Chronicle test data cleared"))
        }
    }

    // ── Time Controls ──────────────────────────────

    fun advanceOneDay() {
        debugTimeProvider?.advanceDays(1)
        refreshState()
        regenerateQuests()
    }

    fun advanceOneWeek() {
        debugTimeProvider?.advanceWeeks(1)
        refreshState()
        regenerateQuests()
    }

    fun advanceOneMonth() {
        debugTimeProvider?.advanceMonths(1)
        refreshState()
        regenerateQuests()
    }

    fun advanceThreeMonths() {
        debugTimeProvider?.advanceMonths(3)
        refreshState()
        regenerateQuests()
    }

    fun advanceOneYear() {
        debugTimeProvider?.advanceYears(1)
        refreshState()
        regenerateQuests()
    }

    fun resetTime() {
        debugTimeProvider?.resetTime()
        refreshState()
        regenerateQuests()
    }

    // ── Chronicle Season Simulation ──────────────────

    fun simulateDecember1() {
        val year = java.time.LocalDate.now().year
        debugTimeProvider?.setToDate("$year-12-01")
        refreshState()
        viewModelScope.launch {
            _events.emit(DevMenuEvent.ShowMessage("Simulated: $year-12-01 (Preparing state)"))
            _events.emit(DevMenuEvent.RestartHome)
        }
    }

    fun simulateDecember24() {
        val year = java.time.LocalDate.now().year
        debugTimeProvider?.setToDate("$year-12-15")
        refreshState()
        viewModelScope.launch {
            _events.emit(DevMenuEvent.ShowMessage("Simulated: $year-12-15 (Ready state)"))
            _events.emit(DevMenuEvent.RestartHome)
        }
    }

    fun simulateJanuary1() {
        val year = java.time.LocalDate.now().year + 1
        debugTimeProvider?.setToDate("$year-01-16")
        refreshState()
        viewModelScope.launch {
            _events.emit(DevMenuEvent.ShowMessage("Simulated: $year-01-16 (Hidden state)"))
            _events.emit(DevMenuEvent.RestartHome)
        }
    }

    // ── Character Controls ──────────────────────────

    fun grantXp(amount: Int) {
        viewModelScope.launch {
            val character = characterRepository.getCharacter().filterNotNull().first()
            characterRepository.updateCharacter(character.copy(totalXp = character.totalXp + amount))
            _events.emit(DevMenuEvent.ShowMessage("+$amount XP granted"))
        }
    }

    fun setRank(targetRank: Int) {
        viewModelScope.launch {
            val xpNeeded = rankService.xpRequiredForRank(targetRank)
            val character = characterRepository.getCharacter().filterNotNull().first()
            characterRepository.updateCharacter(character.copy(totalXp = xpNeeded))
            _events.emit(DevMenuEvent.ShowMessage("Set to Rank $targetRank"))
        }
    }

    fun unlockAllTitles() {
        viewModelScope.launch {
            val titles = titleRepository.getAllTitles().first()
            titles.forEach { title ->
                if (!title.isUnlocked) {
                    titleRepository.unlockTitle(title.id)
                }
            }
            val character = characterRepository.getCharacter().filterNotNull().first()
            val latest = titles.lastOrNull()
            if (latest != null) {
                characterRepository.updateCharacter(character.copy(currentTitle = latest.name))
            }
            _events.emit(DevMenuEvent.ShowMessage("All ${titles.size} titles unlocked"))
        }
    }

    fun unlockAllFeats() {
        viewModelScope.launch {
            val feats = featRepository.getAllFeats().first()
            feats.forEach { feat ->
                if (!feat.isUnlocked) {
                    featRepository.unlockFeat(feat.id)
                }
            }
            _events.emit(DevMenuEvent.ShowMessage("All ${feats.size} achievements unlocked"))
        }
    }

    // ── Quest Controls ──────────────────────────────

    fun generateNewDaily() {
        viewModelScope.launch {
            val character = characterRepository.getCharacter().filterNotNull().first()
            val today = timeProvider.todayDate()
            questRepository.deleteActiveQuestsByType(QuestType.DAILY)
            val quests = questGenerator.generateQuestsForType(character, QuestType.DAILY, 1)
            questRepository.insertActiveQuests(quests.map { quest ->
                ActiveQuest(questId = quest.id, questType = QuestType.DAILY, assignedDate = today, expiresDate = today)
            })
            _events.emit(DevMenuEvent.ShowMessage("New daily quest generated"))
            _events.emit(DevMenuEvent.RestartHome)
        }
    }

    fun generateNewSideQuest() {
        viewModelScope.launch {
            val character = characterRepository.getCharacter().filterNotNull().first()
            val today = timeProvider.todayDate()
            questRepository.deleteActiveQuestsByType(QuestType.SIDE_QUEST)
            val quests = questGenerator.generateQuestsForType(character, QuestType.SIDE_QUEST, 1)
            questRepository.insertActiveQuests(quests.map { quest ->
                ActiveQuest(questId = quest.id, questType = QuestType.SIDE_QUEST, assignedDate = today, expiresDate = timeProvider.addDays(today, 7))
            })
            _events.emit(DevMenuEvent.ShowMessage("New side quest generated"))
            _events.emit(DevMenuEvent.RestartHome)
        }
    }

    fun generateNewAdventure() {
        viewModelScope.launch {
            val character = characterRepository.getCharacter().filterNotNull().first()
            val today = timeProvider.todayDate()
            questRepository.deleteActiveQuestsByType(QuestType.ADVENTURE)
            val quests = questGenerator.generateQuestsForType(character, QuestType.ADVENTURE, 1)
            questRepository.insertActiveQuests(quests.map { quest ->
                ActiveQuest(questId = quest.id, questType = QuestType.ADVENTURE, assignedDate = today, expiresDate = timeProvider.addDays(today, 30))
            })
            _events.emit(DevMenuEvent.ShowMessage("New adventure generated"))
            _events.emit(DevMenuEvent.RestartHome)
        }
    }

    fun generateNewEpic() {
        viewModelScope.launch {
            val character = characterRepository.getCharacter().filterNotNull().first()
            val today = timeProvider.todayDate()
            questRepository.deleteActiveQuestsByType(QuestType.EPIC)
            val quests = questGenerator.generateQuestsForType(character, QuestType.EPIC, 1)
            questRepository.insertActiveQuests(quests.map { quest ->
                ActiveQuest(questId = quest.id, questType = QuestType.EPIC, assignedDate = today, expiresDate = timeProvider.addDays(today, 90))
            })
            _events.emit(DevMenuEvent.ShowMessage("New epic generated"))
            _events.emit(DevMenuEvent.RestartHome)
        }
    }

    // ── Database Controls ───────────────────────────

    fun resetDatabase() {
        viewModelScope.launch {
            characterRepository.deleteCharacter()
            questRepository.deleteAllActiveQuests()
            loreRepository.deleteAllLoreEntries()
            loreRepository.deleteAllCompletedQuests()
            titleRepository.deleteAllTitles()
            featRepository.deleteAllFeats()

            val titles = titleFeatAssetLoader.loadTitles()
            if (titles.isNotEmpty()) titleRepository.insertTitles(titles)
            val feats = titleFeatAssetLoader.loadFeats()
            if (feats.isNotEmpty()) featRepository.insertFeats(feats)

            _events.emit(DevMenuEvent.NavigateToCharacterCreation)
        }
    }

    private fun regenerateQuests() {
        viewModelScope.launch {
            val today = timeProvider.todayDate()
            questRepository.deleteAllActiveQuests()
            try {
                val character = characterRepository.getCharacter().filterNotNull().first()
                val dailyQuests = questGenerator.generateQuestsForType(character, QuestType.DAILY, 1)
                questRepository.insertActiveQuests(dailyQuests.map { quest ->
                    ActiveQuest(questId = quest.id, questType = QuestType.DAILY, assignedDate = today, expiresDate = today)
                })
                val sideQuests = questGenerator.generateQuestsForType(character, QuestType.SIDE_QUEST, 1)
                questRepository.insertActiveQuests(sideQuests.map { quest ->
                    ActiveQuest(questId = quest.id, questType = QuestType.SIDE_QUEST, assignedDate = today, expiresDate = timeProvider.addDays(today, 7))
                })
                val adventures = questGenerator.generateQuestsForType(character, QuestType.ADVENTURE, 1)
                questRepository.insertActiveQuests(adventures.map { quest ->
                    ActiveQuest(questId = quest.id, questType = QuestType.ADVENTURE, assignedDate = today, expiresDate = timeProvider.addDays(today, 30))
                })
                val epics = questGenerator.generateQuestsForType(character, QuestType.EPIC, 1)
                questRepository.insertActiveQuests(epics.map { quest ->
                    ActiveQuest(questId = quest.id, questType = QuestType.EPIC, assignedDate = today, expiresDate = timeProvider.addDays(today, 90))
                })
            } catch (_: Exception) {
                // Character may not exist yet
            }
            _events.emit(DevMenuEvent.RestartHome)
        }
    }
}
