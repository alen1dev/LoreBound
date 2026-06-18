package com.pauls.lorebound.domain.model

/**
 * Data the user submits when completing a quest.
 * Different verification types require different fields to be non-null.
 */
data class VerificationPayload(
    val photoUri: String? = null,
    val text: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val link: String? = null
) {
    val hasPhoto: Boolean get() = !photoUri.isNullOrBlank()
    val hasText: Boolean get() = !text.isNullOrBlank()
    val hasGps: Boolean get() = latitude != null && longitude != null
    val hasLink: Boolean get() = !link.isNullOrBlank()
}

