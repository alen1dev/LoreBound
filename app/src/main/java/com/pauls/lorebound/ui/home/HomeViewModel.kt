package com.pauls.lorebound.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pauls.lorebound.domain.chronicle.ChronicleAvailabilityState
import com.pauls.lorebound.domain.chronicle.ChronicleSeasonManager
import com.pauls.lorebound.domain.model.ActiveQuest
import com.pauls.lorebound.domain.model.Character
import com.pauls.lorebound.domain.model.Chronicle
import com.pauls.lorebound.domain.model.Quest
import com.pauls.lorebound.domain.model.QuestType
import com.pauls.lorebound.domain.repository.CharacterRepository
import com.pauls.lorebound.domain.repository.LoreRepository
import com.pauls.lorebound.domain.repository.QuestRepository
import com.pauls.lorebound.domain.service.QuestGenerator
import com.pauls.lorebound.domain.service.RankService
import com.pauls.lorebound.domain.service.TimeProvider
import com.pauls.lorebound.domain.ai.ChronicleGenerator
import com.pauls.lorebound.domain.ai.ChronicleGenerationResult
import com.pauls.lorebound.domain.ai.ChronicleStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class ActiveQuestWithDetail(
    val activeQuest: ActiveQuest,
    val quest: Quest
)

// Keep legacy type alias for nav compat
data class DailyQuestWithDetail(
    val dailyQuest: com.pauls.lorebound.domain.model.DailyQuest,
    val quest: Quest
)

data class HomeState(
    val character: Character? = null,
    val dailyQuests: List<ActiveQuestWithDetail> = emptyList(),
    val sideQuests: List<ActiveQuestWithDetail> = emptyList(),
    val adventures: List<ActiveQuestWithDetail> = emptyList(),
    val epics: List<ActiveQuestWithDetail> = emptyList(),
    val rank: Int = 1,
    val rankProgress: Float = 0f,
    val xpToNextRank: Long = 0L,
    val isLoading: Boolean = true,
    val questsCompletedToday: Int = 0,
    // Chronicle
    val chronicleState: ChronicleAvailabilityState = ChronicleAvailabilityState.Hidden,
    val chronicleQuestCount: Int = 0,
    val chronicleMemoryCount: Int = 0,
    val chroniclePhotoCount: Int = 0,
    val chroniclePlacesCount: Int = 0,
    val chronicleTitlesEarned: Int = 0,
    val chronicleReady: Chronicle? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val questRepository: QuestRepository,
    private val questGenerator: QuestGenerator,
    private val rankService: RankService,
    private val timeProvider: TimeProvider,
    private val loreRepository: LoreRepository,
    private val chronicleSeasonManager: ChronicleSeasonManager,
    private val chronicleStorage: ChronicleStorage,
    private val chronicleGenerator: ChronicleGenerator
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _refreshMessage = MutableStateFlow<String?>(null)
    val refreshMessage: StateFlow<String?> = _refreshMessage.asStateFlow()

    init {
        viewModelScope.launch {
            val today = timeProvider.todayDate()
            ensureActiveQuestsExist(today)
            loadChronicleData()
            observeState(today)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _refreshMessage.value = null
            val today = timeProvider.todayDate()

            // Track what changed
            val beforeDaily = questRepository.getActiveQuestCountByType(QuestType.DAILY, today)
            
            ensureActiveQuestsExist(today)
            loadChronicleData()
            
            val afterDaily = questRepository.getActiveQuestCountByType(QuestType.DAILY, today)
            
            // Minimum visible duration so the user sees something happened
            kotlinx.coroutines.delay(600)
            
            _refreshMessage.value = if (afterDaily > beforeDaily) {
                "NEW QUESTS FOUND"
            } else {
                "QUESTS UP TO DATE"
            }
            _isRefreshing.value = false
            
            // Clear message after a moment
            kotlinx.coroutines.delay(1500)
            _refreshMessage.value = null
        }
    }

    private suspend fun loadChronicleData() {
        val seasonState = chronicleSeasonManager.getCurrentState()
        val questCount = loreRepository.getCompletedQuestCount()
        val memoryCount = loreRepository.getLoreEntryCount()
        val placesCount = loreRepository.getDistinctLocationCount()

        // Count photos from lore entries
        val allEntries = loreRepository.getAllLoreEntriesList()
        val photoCount = allEntries.count { !it.photoUri.isNullOrBlank() }

        // Auto-generate Chronicle if in season and not yet generated
        val chronicleYear = when (seasonState) {
            is ChronicleAvailabilityState.Preparing -> seasonState.year
            is ChronicleAvailabilityState.Ready -> seasonState.year
            else -> null
        }
        if (chronicleYear != null) {
            val existingChronicle = chronicleStorage.loadChronicle(chronicleYear)
            if (existingChronicle == null) {
                autoGenerateChronicle(chronicleYear)
            }
        }

        // Load chronicle if ready
        val chronicle = if (seasonState is ChronicleAvailabilityState.Ready) {
            chronicleStorage.loadChronicle(seasonState.year)
        } else null

        _state.update {
            it.copy(
                chronicleState = seasonState,
                chronicleQuestCount = questCount,
                chronicleMemoryCount = memoryCount,
                chroniclePhotoCount = photoCount,
                chroniclePlacesCount = placesCount,
                chronicleTitlesEarned = 0,
                chronicleReady = chronicle
            )
        }
    }

    private suspend fun autoGenerateChronicle(year: Int) {
        try {
            val character = characterRepository.getCharacter().filterNotNull().first()
            val completedQuests = loreRepository.getAllCompletedQuests()
            val loreEntries = loreRepository.getAllLoreEntriesList()

            val result = withContext(Dispatchers.IO) {
                chronicleGenerator.generateChronicle(character, completedQuests, loreEntries, year)
            }

            when (result) {
                is ChronicleGenerationResult.Success -> {
                    chronicleStorage.saveChronicleJson(year, result.rawJson)
                }
                is ChronicleGenerationResult.PartialSuccess -> {
                    chronicleStorage.saveChronicleJson(year, result.rawJson)
                }
                is ChronicleGenerationResult.Error -> {
                    // Silent failure — will retry next app open
                }
            }
        } catch (_: Exception) {
            // Silent failure — Chronicle generation is best-effort
        }
    }

    private suspend fun ensureActiveQuestsExist(today: String) {
        val character = characterRepository.getCharacter().filterNotNull().first()

        // Clean up expired quests
        questRepository.clearExpiredQuests(today)

        // Ensure daily quests (refresh every day)
        if (questRepository.getActiveQuestCountByType(QuestType.DAILY, today) == 0) {
            val quests = questGenerator.generateQuestsForType(character, QuestType.DAILY, 1)
            val activeQuests = quests.map { quest ->
                ActiveQuest(
                    questId = quest.id,
                    questType = QuestType.DAILY,
                    assignedDate = today,
                    expiresDate = today
                )
            }
            questRepository.insertActiveQuests(activeQuests)
        }

        // Ensure side quests (refresh every 7 days)
        if (questRepository.getActiveQuestCountByType(QuestType.SIDE_QUEST, today) == 0) {
            val quests = questGenerator.generateQuestsForType(character, QuestType.SIDE_QUEST, 1)
            val expiresDate = timeProvider.addDays(today, 7)
            val activeQuests = quests.map { quest ->
                ActiveQuest(
                    questId = quest.id,
                    questType = QuestType.SIDE_QUEST,
                    assignedDate = today,
                    expiresDate = expiresDate
                )
            }
            questRepository.insertActiveQuests(activeQuests)
        }

        // Ensure adventure (refresh every 30 days)
        if (questRepository.getActiveQuestCountByType(QuestType.ADVENTURE, today) == 0) {
            val quests = questGenerator.generateQuestsForType(character, QuestType.ADVENTURE, 1)
            val expiresDate = timeProvider.addDays(today, 30)
            val activeQuests = quests.map { quest ->
                ActiveQuest(
                    questId = quest.id,
                    questType = QuestType.ADVENTURE,
                    assignedDate = today,
                    expiresDate = expiresDate
                )
            }
            questRepository.insertActiveQuests(activeQuests)
        }

        // Ensure epic (refresh every 90 days)
        if (questRepository.getActiveQuestCountByType(QuestType.EPIC, today) == 0) {
            val quests = questGenerator.generateQuestsForType(character, QuestType.EPIC, 1)
            val expiresDate = timeProvider.addDays(today, 90)
            val activeQuests = quests.map { quest ->
                ActiveQuest(
                    questId = quest.id,
                    questType = QuestType.EPIC,
                    assignedDate = today,
                    expiresDate = expiresDate
                )
            }
            questRepository.insertActiveQuests(activeQuests)
        }

        // Update streak
        val yesterday = timeProvider.yesterdayDate()
        val hadYesterday = questRepository.getActiveQuestCountByType(QuestType.DAILY, yesterday) > 0
        val updatedCharacter = if (character.lastActiveDate == yesterday || character.lastActiveDate == today) {
            character
        } else if (hadYesterday) {
            character.copy(currentStreak = character.currentStreak + 1, lastActiveDate = today)
        } else {
            character.copy(currentStreak = 1, lastActiveDate = today)
        }
        if (updatedCharacter != character) {
            characterRepository.updateCharacter(updatedCharacter)
        }
    }

    private suspend fun observeState(today: String) {
        combine(
            characterRepository.getCharacter().filterNotNull(),
            questRepository.getActiveQuestsByType(QuestType.DAILY, today),
            questRepository.getActiveQuestsByType(QuestType.SIDE_QUEST, today),
            questRepository.getActiveQuestsByType(QuestType.ADVENTURE, today),
            questRepository.getActiveQuestsByType(QuestType.EPIC, today)
        ) { character, dailyActive, sideActive, adventureActive, epicActive ->

            val dailyDetails = dailyActive.mapNotNull { aq ->
                questRepository.getQuestById(aq.questId)?.let { ActiveQuestWithDetail(aq, it) }
            }
            val sideDetails = sideActive.mapNotNull { aq ->
                questRepository.getQuestById(aq.questId)?.let { ActiveQuestWithDetail(aq, it) }
            }
            val adventureDetails = adventureActive.mapNotNull { aq ->
                questRepository.getQuestById(aq.questId)?.let { ActiveQuestWithDetail(aq, it) }
            }
            val epicDetails = epicActive.mapNotNull { aq ->
                questRepository.getQuestById(aq.questId)?.let { ActiveQuestWithDetail(aq, it) }
            }

            val rank = rankService.rankForXp(character.totalXp)
            val progress = rankService.progressToNextRank(character.totalXp)
            val xpToNext = rankService.xpToNextRank(character.totalXp)

            HomeState(
                character = character,
                dailyQuests = dailyDetails,
                sideQuests = sideDetails,
                adventures = adventureDetails,
                epics = epicDetails,
                rank = rank,
                rankProgress = progress,
                xpToNextRank = xpToNext,
                isLoading = false,
                questsCompletedToday = dailyActive.count { it.isCompleted },
                chronicleState = _state.value.chronicleState,
                chronicleQuestCount = _state.value.chronicleQuestCount,
                chronicleMemoryCount = _state.value.chronicleMemoryCount,
                chroniclePhotoCount = _state.value.chroniclePhotoCount,
                chroniclePlacesCount = _state.value.chroniclePlacesCount,
                chronicleTitlesEarned = _state.value.chronicleTitlesEarned,
                chronicleReady = _state.value.chronicleReady
            )
        }.collect { newState ->
            _state.update { newState }
        }
    }
}
