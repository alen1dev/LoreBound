package com.pauls.lorebound.data.seed

import android.content.Context
import android.util.Log
import com.pauls.lorebound.domain.model.Quest
import com.pauls.lorebound.domain.model.QuestType
import com.pauls.lorebound.domain.model.Rarity
import com.pauls.lorebound.domain.model.Trait
import com.pauls.lorebound.domain.model.VerificationType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Loads quest definitions from JSON asset files and maps them to domain Quest objects.
 *
 * Asset files:
 * - assets/daily_quests.json
 * - assets/side_quests.json
 * - assets/adventures.json
 * - assets/epics.json
 */
@Singleton
class QuestAssetLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val assetFiles = listOf(
        "daily_quests.json",
        "side_quests.json",
        "adventures.json",
        "epics.json"
    )

    /**
     * Loads all quests from all asset files.
     * Returns an empty list if all files fail (but logs errors).
     */
    fun loadAllQuests(): List<Quest> {
        return assetFiles.flatMap { fileName ->
            loadQuestsFromAsset(fileName)
        }
    }

    /**
     * Loads quests from a single asset file.
     * Returns empty list on failure.
     */
    private fun loadQuestsFromAsset(fileName: String): List<Quest> {
        return try {
            val jsonString = readAssetFile(fileName)
            if (jsonString.isNullOrBlank()) {
                Log.e(TAG, "Asset file '$fileName' is empty or missing")
                return emptyList()
            }

            val questJsonList = json.decodeFromString<List<QuestJson>>(jsonString)
            if (questJsonList.isEmpty()) {
                Log.w(TAG, "Asset file '$fileName' contains no quests")
                return emptyList()
            }

            questJsonList.mapNotNull { questJson ->
                mapToDomain(questJson, fileName)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load quests from '$fileName': ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Reads raw text content from an asset file.
     */
    private fun readAssetFile(fileName: String): String? {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            Log.e(TAG, "Cannot open asset file '$fileName': ${e.message}")
            null
        }
    }

    /**
     * Maps a QuestJson DTO to a domain Quest object.
     * Returns null if required fields cannot be parsed.
     */
    private fun mapToDomain(dto: QuestJson, sourceFile: String): Quest? {
        return try {
            Quest(
                slug = dto.id,
                title = dto.title,
                description = dto.description,
                questType = parseEnum<QuestType>(dto.questType, "questType", dto.title),
                primaryAttribute = parseEnum<Trait>(dto.primaryAttribute, "primaryAttribute", dto.title),
                secondaryAttribute = dto.secondaryAttribute?.let {
                    try {
                        enumValueOf<Trait>(it)
                    } catch (e: IllegalArgumentException) {
                        Log.w(TAG, "Unknown secondaryAttribute '$it' in quest '${dto.title}' ($sourceFile)")
                        null
                    }
                },
                xpReward = dto.xpReward,
                difficulty = dto.difficulty.coerceIn(1, 5),
                estimatedMinutes = dto.estimatedMinutes,
                durationDays = dto.durationDays,
                storyWeight = dto.storyWeight.coerceIn(1, 10),
                rarity = parseEnum<Rarity>(dto.rarity, "rarity", dto.title),
                verificationType = resolveVerificationType(dto, sourceFile),
                tags = dto.tags
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to map quest '${dto.title}' from '$sourceFile': ${e.message}")
            null
        }
    }

    /**
     * Resolves verification type from either new nested format or legacy flat field.
     * New format: { "verification": { "type": "PHOTO_AND_TEXT" } }
     * Legacy format: { "verificationType": "PHOTO" }
     */
    private fun resolveVerificationType(dto: QuestJson, sourceFile: String): VerificationType {
        // Prefer new nested verification object
        val typeString = dto.verification?.type ?: dto.verificationType
        if (typeString.isNullOrBlank()) {
            Log.w(TAG, "No verification type in quest '${dto.title}' ($sourceFile), defaulting to MANUAL")
            return VerificationType.MANUAL
        }
        return try {
            enumValueOf<VerificationType>(typeString)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Unknown verificationType '$typeString' in quest '${dto.title}' ($sourceFile), defaulting to MANUAL")
            VerificationType.MANUAL
        }
    }

    /**
     * Parses a string to an enum value with helpful error logging.
     */
    private inline fun <reified T : Enum<T>> parseEnum(
        value: String,
        fieldName: String,
        questTitle: String
    ): T {
        return try {
            enumValueOf<T>(value)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Invalid $fieldName '$value' in quest '$questTitle'. " +
                        "Valid values: ${enumValues<T>().joinToString { it.name }}"
            )
        }
    }

    companion object {
        private const val TAG = "QuestAssetLoader"
    }
}

