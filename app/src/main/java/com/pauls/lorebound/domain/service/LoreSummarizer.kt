package com.pauls.lorebound.domain.service

import com.pauls.lorebound.domain.model.LoreEntry

interface LoreSummarizer {
    suspend fun summarize(entries: List<LoreEntry>): String
}

