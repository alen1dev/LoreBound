package com.pauls.lorebound.domain.service

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XpService @Inject constructor() {


    fun xpForDifficultyLevel(level: Int): Int = when (level) {
        1 -> 25
        2 -> 50
        3 -> 100
        4 -> 250
        5 -> 500
        else -> 0
    }
}

