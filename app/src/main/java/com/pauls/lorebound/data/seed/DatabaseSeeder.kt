package com.pauls.lorebound.data.seed

import android.content.Context
import android.util.Log
import com.pauls.lorebound.domain.repository.FeatRepository
import com.pauls.lorebound.domain.repository.QuestRepository
import com.pauls.lorebound.domain.repository.TitleRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val questRepository: QuestRepository,
    private val titleRepository: TitleRepository,
    private val featRepository: FeatRepository,
    private val questAssetLoader: QuestAssetLoader,
    private val titleFeatAssetLoader: TitleFeatAssetLoader
) {
    /**
     * Increment this whenever quest JSON content changes.
     * This triggers a reseed of quest data on next app launch.
     */
    companion object {
        private const val TAG = "DatabaseSeeder"
        private const val PREFS_NAME = "lorebound_seeder"
        private const val KEY_QUEST_CONTENT_VERSION = "quest_content_version"
        private const val CURRENT_QUEST_CONTENT_VERSION = 2 // Bumped: verification refactor
    }

    suspend fun seedIfEmpty() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val storedVersion = prefs.getInt(KEY_QUEST_CONTENT_VERSION, 0)

        // Reseed quests if content version has changed or if DB is empty
        if (storedVersion < CURRENT_QUEST_CONTENT_VERSION || questRepository.getQuestCount() == 0) {
            val quests = questAssetLoader.loadAllQuests()
            if (quests.isNotEmpty()) {
                questRepository.insertQuests(quests)
                prefs.edit().putInt(KEY_QUEST_CONTENT_VERSION, CURRENT_QUEST_CONTENT_VERSION).apply()
                Log.d(TAG, "Seeded ${quests.size} quests from asset files (version $CURRENT_QUEST_CONTENT_VERSION)")
            } else {
                Log.e(TAG, "No quests loaded from assets — database will be empty")
            }
        }

        if (titleRepository.getTitleCount() == 0) {
            val titles = titleFeatAssetLoader.loadTitles()
            if (titles.isNotEmpty()) {
                titleRepository.insertTitles(titles)
                Log.d(TAG, "Seeded ${titles.size} titles from assets/titles.json")
            } else {
                Log.e(TAG, "No titles loaded from assets")
            }
        }
        if (featRepository.getFeatCount() == 0) {
            val feats = titleFeatAssetLoader.loadFeats()
            if (feats.isNotEmpty()) {
                featRepository.insertFeats(feats)
                Log.d(TAG, "Seeded ${feats.size} achievements from assets/achievements.json")
            } else {
                Log.e(TAG, "No achievements loaded from assets")
            }
        }
    }
}
