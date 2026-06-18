package com.pauls.lorebound.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Chronicle(
    val version: Int = 1,
    val yearTitle: String = "",
    val mainTheme: String = "",
    val bestMonth: String = "",
    val totalQuestsCompleted: Int = 0,
    val totalXpEarned: Int = 0,
    val totalLoreEntries: Int = 0,
    val topAttributes: List<String> = emptyList(),
    val slides: List<ChronicleSlide> = emptyList()
)

@Serializable
data class ChronicleSlide(
    val slideType: String = "",
    val title: String = "",
    val subtitle: String = "",
    val body: String = "",
    val statLabel: String? = null,
    val statValue: String? = null,
    val imageUri: String? = null,
    val backgroundColor: String? = null
)

