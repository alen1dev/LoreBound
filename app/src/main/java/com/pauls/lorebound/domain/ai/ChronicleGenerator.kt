package com.pauls.lorebound.domain.ai

import com.pauls.lorebound.domain.model.Chronicle
import com.pauls.lorebound.domain.model.CompletedQuest
import com.pauls.lorebound.domain.model.LoreEntry
import com.pauls.lorebound.domain.model.Character
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates Chronicle JSON from user data using AI or fallback local logic.
 */
@Singleton
class ChronicleGenerator @Inject constructor(
    private val geminiProvider: GeminiProvider,
    private val aiKeyStore: AiKeyStore
) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    suspend fun generateChronicle(
        character: Character,
        completedQuests: List<CompletedQuest>,
        loreEntries: List<LoreEntry>,
        year: Int
    ): ChronicleGenerationResult {
        val provider = aiKeyStore.getSelectedProvider()

        if (provider == AiProviderType.DISABLED || !geminiProvider.isConfigured) {
            return ChronicleGenerationResult.Error("AI is disabled or not configured")
        }

        val prompt = buildPrompt(character, completedQuests, loreEntries, year)
        return when (val result = geminiProvider.generateChronicleJson(prompt)) {
            is AiGenerationResult.Success -> {
                try {
                    val chronicle = json.decodeFromString<Chronicle>(result.json)
                    ChronicleGenerationResult.Success(chronicle, result.json)
                } catch (e: Exception) {
                    // Try to return raw JSON even if parsing fails
                    ChronicleGenerationResult.PartialSuccess(result.json, "Parsed JSON but failed to map: ${e.message}")
                }
            }
            is AiGenerationResult.Error -> {
                ChronicleGenerationResult.Error(result.message)
            }
        }
    }

    private fun buildPrompt(
        character: Character,
        completedQuests: List<CompletedQuest>,
        loreEntries: List<LoreEntry>,
        year: Int
    ): String {
        val loreSummary = loreEntries.take(50).joinToString("\n") { "- ${it.questTitle}: ${it.userNotes?.take(100) ?: ""}" }
        val attributes = listOf("STRENGTH", "INTELLIGENCE", "CHARISMA", "CREATIVITY", "EXPLORATION", "COURAGE")

        return """
            You are generating a Year-End Chronicle for a gamified real-life adventure app called Lorebound.

            PLAYER: ${character.name}
            TITLE: ${character.currentTitle ?: "Wanderer"}
            YEAR: $year
            TOTAL XP: ${character.totalXp}
            QUESTS COMPLETED: ${completedQuests.size}
            LORE ENTRIES: ${loreEntries.size}
            
            LORE ENTRIES (user memories):
            $loreSummary

            Generate a structured Chronicle JSON with this EXACT schema:
            {
                "version": 1,
                "yearTitle": "The Year of [Theme]",
                "mainTheme": "[one of: $attributes]",
                "bestMonth": "[month name]",
                "totalQuestsCompleted": ${completedQuests.size},
                "totalXpEarned": ${character.totalXp},
                "totalLoreEntries": ${loreEntries.size},
                "topAttributes": ["ATTRIBUTE1", "ATTRIBUTE2", "ATTRIBUTE3"],
                "slides": [
                    {
                        "slideType": "TITLE|STAT|HIGHLIGHT|QUOTE|SUMMARY",
                        "title": "...",
                        "subtitle": "...",
                        "body": "...",
                        "statLabel": null,
                        "statValue": null,
                        "imageUri": null,
                        "backgroundColor": null
                    }
                ]
            }

            Generate exactly 8-12 slides. Make them feel epic, personal, and memoir-worthy.
            Slide types: TITLE (opening), STAT (number highlight), HIGHLIGHT (best quest/moment), QUOTE (reflective), SUMMARY (closing).
            
            Return ONLY valid JSON. No markdown, no explanation, no code fences.
        """.trimIndent()
    }
}

sealed interface ChronicleGenerationResult {
    data class Success(val chronicle: Chronicle, val rawJson: String) : ChronicleGenerationResult
    data class PartialSuccess(val rawJson: String, val message: String) : ChronicleGenerationResult
    data class Error(val message: String) : ChronicleGenerationResult
}




