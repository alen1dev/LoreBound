package com.pauls.lorebound.data.seed

import kotlinx.serialization.Serializable

/**
 * JSON-serializable title DTO.
 * Maps directly to the JSON schema in assets/titles.json.
 */
@Serializable
data class TitleJson(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val requiredQuestCount: Int,
    val requiredCategory: String? = null
)

