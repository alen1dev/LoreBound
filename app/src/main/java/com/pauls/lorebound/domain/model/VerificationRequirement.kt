package com.pauls.lorebound.domain.model

/**
 * Describes the full verification requirement for a quest.
 * Derived from VerificationType but provides structured access
 * for UI rendering and validation logic.
 */
data class VerificationRequirement(
    val type: VerificationType,
    val needsPhoto: Boolean = type.requiresPhoto,
    val needsText: Boolean = type.requiresText,
    val needsGps: Boolean = type.requiresGps,
    val needsLink: Boolean = type.requiresLink,
    val isOptional: Boolean = type.isOptional
) {
    companion object {
        fun from(type: VerificationType): VerificationRequirement =
            VerificationRequirement(type = type)
    }
}

