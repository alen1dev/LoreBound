package com.pauls.lorebound.domain.model

/**
 * Result of validating a VerificationPayload against a VerificationRequirement.
 */
sealed class VerificationResult {
    data object Success : VerificationResult()

    data class Failure(
        val missingPhoto: Boolean = false,
        val missingText: Boolean = false,
        val missingGps: Boolean = false,
        val missingLink: Boolean = false,
        val message: String = buildMessage(missingPhoto, missingText, missingGps, missingLink)
    ) : VerificationResult() {
        companion object {
            private fun buildMessage(
                photo: Boolean,
                text: Boolean,
                gps: Boolean,
                link: Boolean
            ): String {
                val missing = mutableListOf<String>()
                if (photo) missing.add("photo")
                if (text) missing.add("lore entry")
                if (gps) missing.add("location")
                if (link) missing.add("link")
                return if (missing.isEmpty()) "Verification failed"
                else "Missing: ${missing.joinToString(", ")}"
            }
        }
    }

    val isSuccess: Boolean get() = this is Success
}

