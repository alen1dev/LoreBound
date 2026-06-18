package com.pauls.lorebound.domain.chronicle

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks whether the user has viewed their Chronicle this season.
 * When true (and within Dec 15 – Jan 15), the starry background appears on all pages.
 */
@Singleton
class ChronicleStarsPreference @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs by lazy {
        context.getSharedPreferences("chronicle_stars", Context.MODE_PRIVATE)
    }

    private val KEY_VIEWED_YEAR = "chronicle_viewed_year"

    fun markChronicleViewed(year: Int) {
        prefs.edit().putInt(KEY_VIEWED_YEAR, year).apply()
    }

    fun hasViewedChronicle(year: Int): Boolean {
        return prefs.getInt(KEY_VIEWED_YEAR, -1) == year
    }

    fun clearViewed() {
        prefs.edit().remove(KEY_VIEWED_YEAR).apply()
    }
}

