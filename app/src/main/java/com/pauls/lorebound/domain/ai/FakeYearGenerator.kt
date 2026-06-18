package com.pauls.lorebound.domain.ai

import com.pauls.lorebound.domain.model.CompletedQuest
import com.pauls.lorebound.domain.model.LoreEntry
import com.pauls.lorebound.domain.model.Trait
import com.pauls.lorebound.domain.model.VerificationType
import com.pauls.lorebound.domain.repository.CharacterRepository
import com.pauls.lorebound.domain.repository.LoreRepository
import com.pauls.lorebound.domain.repository.TitleRepository
import com.pauls.lorebound.domain.service.TimeProvider
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Generates fake year data for Chronicle testing.
 * Creates realistic quest completions, lore entries, and locations.
 */
@Singleton
class FakeYearGenerator @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val loreRepository: LoreRepository,
    private val titleRepository: TitleRepository,
    private val timeProvider: TimeProvider
) {
    private val loreTitles = listOf(
        "The sunset over the harbor", "A stranger's kindness", "Lost in the old quarter",
        "The best coffee I've ever had", "Rain at the temple", "An unexpected conversation",
        "The view from the hill", "Dancing in the street", "A moment of silence",
        "The sound of waves", "Found a secret garden", "The old bookshop owner",
        "Stars over the campsite", "A handwritten letter", "The taste of freedom",
        "Morning fog on the river", "The musician in the square", "A door left open",
        "The weight of history", "Fireflies at dusk", "Visited a museum",
        "Tried street food", "Explored a new neighborhood", "Attended a concert",
        "Photographed sunrise", "Cooked a foreign dish", "Talked to a stranger",
        "Visited a bookshop", "Walked a new trail", "Took a ferry ride"
    )

    private val locations = listOf(
        "Downtown Museum", "Riverside Park", "Old Town Square", "Harbor District",
        "Mountain Lookout", "University Quarter", "Central Market", "Botanical Garden",
        "Waterfront Pier", "Art District", "Historic Cathedral", "Night Market",
        "Hilltop Temple", "Underground Tunnel", "Rooftop Bar", "Hidden Courtyard",
        "Abandoned Factory", "Lighthouse Point", "Forest Trail", "Island Ferry Terminal"
    )

    suspend fun generateFakeYear(): FakeYearResult {
        val character = characterRepository.getCharacter().filterNotNull().first()
        val traits = Trait.entries.toList()
        var questsCreated = 0
        var loreCreated = 0
        var locationsCreated = 0
        var photosCreated = 0
        var titlesUnlocked = 0

        // Generate 100 quest completions
        repeat(100) {
            loreRepository.insertCompletedQuest(
                CompletedQuest(
                    questId = Random.nextLong(1, 200),
                    completedAt = System.currentTimeMillis() - Random.nextLong(1L, 365L * 24 * 60 * 60 * 1000),
                    verificationType = VerificationType.MANUAL,
                    photoUri = if (it < 15) "content://media/external/images/media/${Random.nextInt(1000, 9999)}" else null,
                    latitude = if (it < 20) 48.0 + Random.nextDouble(-2.0, 2.0) else null,
                    longitude = if (it < 20) 11.0 + Random.nextDouble(-2.0, 2.0) else null
                )
            )
            questsCreated++
            if (it < 15) photosCreated++
            if (it < 20) locationsCreated++
        }

        // Generate 50 lore entries
        repeat(50) { i ->
            val hasPhoto = i < 15
            val hasLocation = i < 20
            val location = if (hasLocation) locations[i % locations.size] else null

            loreRepository.insertLoreEntry(
                LoreEntry(
                    questId = if (i < 30) Random.nextLong(1, 100) else 0L,
                    date = timeProvider.addDays(timeProvider.todayDate(), -Random.nextInt(1, 365)),
                    questTitle = loreTitles[i % loreTitles.size],
                    xpEarned = Random.nextInt(25, 200),
                    traitsImproved = listOf(traits.random()),
                    userNotes = "A memorable moment from my adventure. The world felt different today — like every step was part of something bigger.",
                    isPersonal = i >= 30,
                    tags = listOf("adventure", "exploration", "memorable").shuffled().take(2),
                    isFavorite = i < 10,
                    photoUri = if (hasPhoto) "content://media/external/images/media/${Random.nextInt(1000, 9999)}" else null,
                    latitude = if (hasLocation) 48.0 + Random.nextDouble(-2.0, 2.0) else null,
                    longitude = if (hasLocation) 11.0 + Random.nextDouble(-2.0, 2.0) else null,
                    locationName = location,
                    storyWeight = Random.nextInt(3, 10)
                )
            )
            loreCreated++
        }

        // Unlock 5 titles
        val titles = titleRepository.getAllTitles().first()
        titles.take(5).forEach { title ->
            if (!title.isUnlocked) {
                titleRepository.unlockTitle(title.id)
                titlesUnlocked++
            }
        }

        // Grant XP to character
        val totalXpGained = Random.nextInt(3000, 8000)
        characterRepository.updateCharacter(
            character.copy(totalXp = character.totalXp + totalXpGained)
        )

        return FakeYearResult(
            questsCompleted = questsCreated,
            loreEntries = loreCreated,
            locations = locationsCreated,
            photos = photosCreated,
            titlesUnlocked = titlesUnlocked,
            xpGranted = totalXpGained
        )
    }
}

data class FakeYearResult(
    val questsCompleted: Int,
    val loreEntries: Int,
    val locations: Int,
    val photos: Int,
    val titlesUnlocked: Int,
    val xpGranted: Int
)



