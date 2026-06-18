package com.pauls.lorebound.data.seed

import kotlinx.serialization.Serializable

/**
 * JSON-serializable feat/achievement DTO.
 * Maps directly to the JSON schema in assets/achievements.json.
 */
@Serializable
data class FeatJson(
    val id: String,
    val name: String,
    val description: String,
    val requirement: String,
    val targetValue: Int
)

