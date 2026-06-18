package com.pauls.lorebound.domain.service

import com.pauls.lorebound.domain.model.VerificationPayload
import com.pauls.lorebound.domain.model.VerificationRequirement
import com.pauls.lorebound.domain.model.VerificationResult
import com.pauls.lorebound.domain.model.VerificationType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Validates quest completion payloads against verification requirements.
 *
 * Future extensions:
 * - AI image verification (validate photo content matches quest)
 * - GPS radius validation (check user is within X metres of target)
 * - Chronicle contribution routing (photo → gallery, text → lore, GPS → map)
 * - Community event verification (multiple users confirm same event)
 */
@Singleton
class QuestVerificationService @Inject constructor() {

    /**
     * Validates a payload against the quest's verification requirement.
     */
    fun validate(
        requirement: VerificationRequirement,
        payload: VerificationPayload
    ): VerificationResult {
        return when (requirement.type) {
            VerificationType.NONE,
            VerificationType.MANUAL -> VerificationResult.Success

            VerificationType.PHOTO -> {
                if (payload.hasPhoto) VerificationResult.Success
                else VerificationResult.Failure(missingPhoto = true)
            }

            VerificationType.GPS -> {
                if (payload.hasGps) VerificationResult.Success
                else VerificationResult.Failure(missingGps = true)
            }

            VerificationType.TEXT -> {
                if (payload.hasText) VerificationResult.Success
                else VerificationResult.Failure(missingText = true)
            }

            VerificationType.LINK -> {
                if (payload.hasLink) VerificationResult.Success
                else VerificationResult.Failure(missingLink = true)
            }

            VerificationType.PHOTO_OR_TEXT -> {
                if (payload.hasPhoto || payload.hasText) VerificationResult.Success
                else VerificationResult.Failure(missingPhoto = true, missingText = true)
            }

            VerificationType.PHOTO_AND_TEXT -> {
                val missingPhoto = !payload.hasPhoto
                val missingText = !payload.hasText
                if (!missingPhoto && !missingText) VerificationResult.Success
                else VerificationResult.Failure(
                    missingPhoto = missingPhoto,
                    missingText = missingText
                )
            }

            VerificationType.GPS_AND_PHOTO -> {
                val missingGps = !payload.hasGps
                val missingPhoto = !payload.hasPhoto
                if (!missingGps && !missingPhoto) VerificationResult.Success
                else VerificationResult.Failure(
                    missingGps = missingGps,
                    missingPhoto = missingPhoto
                )
            }
        }
    }

    /**
     * Returns the Chronicle contribution types for a given verification type.
     * Used when generating Chronicles from completed quests.
     */
    fun chronicleContributions(type: VerificationType): Set<ChronicleContribution> {
        val contributions = mutableSetOf<ChronicleContribution>()
        if (type.requiresPhoto) contributions.add(ChronicleContribution.IMAGE)
        if (type.requiresText) contributions.add(ChronicleContribution.LORE_ENTRY)
        if (type.requiresGps) contributions.add(ChronicleContribution.LOCATION)
        if (type.requiresLink) contributions.add(ChronicleContribution.LINK)
        return contributions
    }
}

/**
 * Types of contributions a completed quest can make to a Chronicle.
 */
enum class ChronicleContribution {
    IMAGE,
    LORE_ENTRY,
    LOCATION,
    LINK
}

