package com.pauls.lorebound.domain.service

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.floor
import kotlin.math.pow

@Singleton
class RankService @Inject constructor() {

    companion object {
        private const val BASE_XP = 100.0
        private const val SCALE_FACTOR = 1.5
    }

    fun rankForXp(totalXp: Long): Int {
        if (totalXp <= 0) return 1
        var rank = 1
        while (xpRequiredForRank(rank + 1) <= totalXp) {
            rank++
        }
        return rank
    }

    fun xpRequiredForRank(rank: Int): Long {
        if (rank <= 1) return 0L
        return floor(BASE_XP * (rank - 1.0).pow(SCALE_FACTOR)).toLong()
    }

    fun xpToNextRank(totalXp: Long): Long {
        val currentRank = rankForXp(totalXp)
        val nextRankXp = xpRequiredForRank(currentRank + 1)
        return nextRankXp - totalXp
    }

    fun progressToNextRank(totalXp: Long): Float {
        val currentRank = rankForXp(totalXp)
        val currentRankXp = xpRequiredForRank(currentRank)
        val nextRankXp = xpRequiredForRank(currentRank + 1)
        val rangeXp = nextRankXp - currentRankXp
        if (rangeXp <= 0) return 1f
        return ((totalXp - currentRankXp).toFloat() / rangeXp.toFloat()).coerceIn(0f, 1f)
    }
}

