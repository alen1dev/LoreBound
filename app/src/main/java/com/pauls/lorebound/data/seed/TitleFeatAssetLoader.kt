package com.pauls.lorebound.data.seed

import android.content.Context
import android.util.Log
import com.pauls.lorebound.domain.model.Feat
import com.pauls.lorebound.domain.model.FeatRequirement
import com.pauls.lorebound.domain.model.QuestCategory
import com.pauls.lorebound.domain.model.Title
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Loads title and achievement definitions from JSON asset files
 * and maps them to domain objects.
 *
 * Asset files:
 * - assets/titles.json
 * - assets/achievements.json
 */
@Singleton
class TitleFeatAssetLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun loadTitles(): List<Title> {
        return try {
            val jsonString = readAssetFile("titles.json") ?: return emptyList()
            val dtos = json.decodeFromString<List<TitleJson>>(jsonString)
            dtos.mapNotNull { dto ->
                try {
                    Title(
                        id = dto.id,
                        name = dto.name,
                        description = dto.description,
                        category = enumValueOf<QuestCategory>(dto.category),
                        requiredQuestCount = dto.requiredQuestCount,
                        requiredCategory = dto.requiredCategory?.let {
                            enumValueOf<QuestCategory>(it)
                        }
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to map title '${dto.id}': ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load titles from assets: ${e.message}", e)
            emptyList()
        }
    }

    fun loadFeats(): List<Feat> {
        return try {
            val jsonString = readAssetFile("achievements.json") ?: return emptyList()
            val dtos = json.decodeFromString<List<FeatJson>>(jsonString)
            dtos.mapNotNull { dto ->
                try {
                    Feat(
                        id = dto.id,
                        name = dto.name,
                        description = dto.description,
                        requirement = enumValueOf<FeatRequirement>(dto.requirement),
                        targetValue = dto.targetValue
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to map achievement '${dto.id}': ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load achievements from assets: ${e.message}", e)
            emptyList()
        }
    }

    private fun readAssetFile(fileName: String): String? {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            Log.e(TAG, "Cannot open asset file '$fileName': ${e.message}")
            null
        }
    }

    companion object {
        private const val TAG = "TitleFeatAssetLoader"
    }
}

