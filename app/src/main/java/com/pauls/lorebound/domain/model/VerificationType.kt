package com.pauls.lorebound.domain.model

/**
 * Defines what evidence a user must provide to complete a quest.
 */
enum class VerificationType(val displayName: String) {
    NONE("None"),
    MANUAL("Manual"),
    PHOTO("Photo"),
    GPS("Location"),
    TEXT("Lore Entry"),
    LINK("Link"),
    PHOTO_OR_TEXT("Photo or Lore Entry"),
    PHOTO_AND_TEXT("Photo + Lore Entry"),
    GPS_AND_PHOTO("Location + Photo");

    /** Whether this type requires a photo attachment */
    val requiresPhoto: Boolean
        get() = this in listOf(PHOTO, PHOTO_AND_TEXT, GPS_AND_PHOTO, PHOTO_OR_TEXT)

    /** Whether this type requires written text */
    val requiresText: Boolean
        get() = this in listOf(TEXT, PHOTO_AND_TEXT, PHOTO_OR_TEXT)

    /** Whether this type requires GPS location */
    val requiresGps: Boolean
        get() = this in listOf(GPS, GPS_AND_PHOTO)

    /** Whether this type requires a URL */
    val requiresLink: Boolean
        get() = this == LINK

    /** Whether the user can skip verification (just tap complete) */
    val isOptional: Boolean
        get() = this in listOf(NONE, MANUAL)

    /** Human-readable description for the quest detail UI */
    val requirementLabel: String
        get() = when (this) {
            NONE -> "No verification needed"
            MANUAL -> "Tap to complete"
            PHOTO -> "Photo required"
            GPS -> "Location check-in required"
            TEXT -> "Lore entry required"
            LINK -> "Link required"
            PHOTO_OR_TEXT -> "Photo or lore entry required"
            PHOTO_AND_TEXT -> "Photo and lore entry required"
            GPS_AND_PHOTO -> "Location check-in and photo required"
        }
}

